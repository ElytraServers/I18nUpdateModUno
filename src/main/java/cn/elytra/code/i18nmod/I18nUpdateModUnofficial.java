package cn.elytra.code.i18nmod;

import cn.elytra.code.i18nmod.config.I18nConfig;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static cn.elytra.code.i18nmod.config.I18nConfig.*;

@Mod(I18nUpdateModUnofficial.MODID)
public class I18nUpdateModUnofficial {

	public static final String MODID = "i18n_update_mod_uno";
	public static final Path CACHE_DIR = Paths.get(System.getProperty("user.home"), MODID, "1.18");
	public static final Logger LOGGER = LogManager.getLogger();

	public I18nUpdateModUnofficial() {
		LOGGER.info("正在加载 I18nUpdateMod 非官方版");

		// 准备配置文档
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, I18nConfig.CONFIG);

		// 设置语言为简体中文
		Minecraft.getInstance().options.languageCode = "zh_cn";

		if(!Files.isDirectory(CACHE_DIR)) {
			try {
				Files.createDirectories(CACHE_DIR);
			} catch(IOException ex) {
				LOGGER.error("无法创建缓存文件夹", ex);
				return;
			}
		}

		if(isCacheExists()) {
			long interval = getIntervalSinceCacheLastModification();
			if(interval > 0) {
				if(interval >= getMaxIntervalDays()) { // 此部分需要测试
					LOGGER.info("正在更新翻译包");
					downloadAndCache();
				}
				addLanguagePackFinder();
			}
		} else {
			downloadAndCache();
			addLanguagePackFinder();
		}
	}

	static boolean isCacheExists() {
		return Files.exists(getCacheFilePath());
	}

	/**
	 * 获取翻译包缓存时间（单位：日）
	 * @return 翻译包缓存时间（-1 错误，-2 翻译包不存在）
	 */
	static long getIntervalSinceCacheLastModification() {
		if(isCacheExists()) {
			try {
				long fileTime = Files.getLastModifiedTime(getCacheFilePath()).toMillis();
				long nowTime  = System.currentTimeMillis();
				return TimeUnit.MICROSECONDS.toDays(nowTime - fileTime);
			} catch(IOException ex) {
				LOGGER.warn("无法获取翻译包缓存时间", ex);
				return -1;
			}
		} else {
			return -2;
		}
	}

	static void downloadAndCache() {
		if(isCacheExists()) {
			try { // 重命名旧包
				Files.move(getCacheFilePath(), getCacheFilePath().getParent().resolve(SimpleDateFormat.getInstance().format(new Date())+"-OldPack.zip"));
			} catch(IOException ex) {
				try {
					Files.delete(getCacheFilePath());
					LOGGER.warn("无法重命名旧翻译包，已删除", ex);
				} catch(IOException ex2) {
					LOGGER.error("无法重命名旧翻译包，且无法删除", ex2);
					return;
				}
			}
		}

		try {
			FileUtils.copyURLToFile(new URL(getDownloadUrl()), getCacheFilePath().toFile());
			LOGGER.info("新翻译包已缓存于 {}", getCacheFilePath());
		} catch(IOException ex) {
			LOGGER.error("无法下载翻译包", ex);
		}

		// Minecraft.getInstance().getResourcePackRepository().addPackFinder(new LanguagePackFinder());
	}

	static void addLanguagePackFinder() {
		Minecraft.getInstance().getResourcePackRepository().addPackFinder(new LanguagePackFinder());
	}
}
