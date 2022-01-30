package cn.elytra.code.i18nmod;

import com.mojang.authlib.minecraft.client.MinecraftClient;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.Mod;
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

@Mod(I18nUpdateModUnofficial.MODID)
public class I18nUpdateModUnofficial {

	public static final String MODID = "i18n_update_mod_uno";
	public static final Path CACHE_DIR = Paths.get(System.getProperty("user.home"), MODID, "1.18");
	public static final Path LANGUAGE_PACK = CACHE_DIR.resolve("Minecraft-Mod-Language-Modpack-1.16.zip");
	public static final String LINK = "http://downloader1.meitangdehulu.com:22943/Minecraft-Mod-Language-Modpack-1-16.zip";
	public static final long MAX_INTERVAL_DAYS = 7;
	public static final Logger LOGGER = LogManager.getLogger();

	public I18nUpdateModUnofficial() {
		LOGGER.info("正在加载 I18nUpdateMod 非官方版");

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
				if(interval >= MAX_INTERVAL_DAYS) { // 此部分需要测试
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
		return Files.exists(LANGUAGE_PACK);
	}

	/**
	 * 获取翻译包缓存时间（单位：日）
	 * @return 翻译包缓存时间（-1 错误，-2 翻译包不存在）
	 */
	static long getIntervalSinceCacheLastModification() {
		if(isCacheExists()) {
			try {
				long fileTime = Files.getLastModifiedTime(LANGUAGE_PACK).toMillis();
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

	static boolean downloadAndCache() {
		if(isCacheExists()) {
			try { // 重命名旧包
				Files.move(LANGUAGE_PACK, LANGUAGE_PACK.getParent().resolve(SimpleDateFormat.getInstance().format(new Date())+"-OldPack.zip"));
			} catch(IOException ex) {
				try {
					Files.delete(LANGUAGE_PACK);
					LOGGER.warn("无法重命名旧翻译包，已删除", ex);
				} catch(IOException ex2) {
					LOGGER.error("无法重命名旧翻译包，且无法删除", ex2);
					return false;
				}
			}
		}

		try {
			FileUtils.copyURLToFile(new URL(LINK), LANGUAGE_PACK.toFile());
			LOGGER.info("新翻译包已缓存于 {}", LANGUAGE_PACK);
			return true;
		} catch(IOException ex) {
			LOGGER.error("无法下载翻译包", ex);
			return false;
		}

		// Minecraft.getInstance().getResourcePackRepository().addPackFinder(new LanguagePackFinder());
	}

	static void addLanguagePackFinder() {
		Minecraft.getInstance().getResourcePackRepository().addPackFinder(new LanguagePackFinder());
	}
}
