package cn.elytra.code.i18nmod.config;

import cn.elytra.code.i18nmod.I18nUpdateModUnofficial;
import net.minecraftforge.common.ForgeConfigSpec;

import java.nio.file.Path;

public class I18nConfig {

	private static final ForgeConfigSpec.ConfigValue<String> PROP_DOWNLOAD_URL;
	private static final ForgeConfigSpec.ConfigValue<String> PROP_CACHE_FILE_NAME;
	private static final ForgeConfigSpec.ConfigValue<Integer> PROP_MAX_INTERVAL_DAYS;

	public final static ForgeConfigSpec CONFIG;

	static {
		final ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

		PROP_DOWNLOAD_URL = builder
				.comment("用于下载翻译包的链接")
				.define("downloadUrl", "http://downloader1.meitangdehulu.com:22943/Minecraft-Mod-Language-Modpack-1-16.zip");

		PROP_CACHE_FILE_NAME = builder
				.comment("本地保存的翻译包缓存名称")
				.define("cacheFileName", "Minecraft-Mod-Language-Modpack-1.16.zip");

		PROP_MAX_INTERVAL_DAYS = builder
				.comment("maxIntervalDays")
				.define("maxIntervalDays", 7);

		CONFIG = builder.build();
	}

	public static String getDownloadUrl() {
		return PROP_DOWNLOAD_URL.get();
	}

	public static String getCacheFileName() {
		return PROP_CACHE_FILE_NAME.get();
	}

	public static int getMaxIntervalDays() {
		return PROP_MAX_INTERVAL_DAYS.get();
	}

	public static Path getCacheFilePath() {
		return I18nUpdateModUnofficial.CACHE_DIR.resolve(getCacheFileName());
	}

}
