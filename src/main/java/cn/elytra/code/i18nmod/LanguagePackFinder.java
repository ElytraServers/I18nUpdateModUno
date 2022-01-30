package cn.elytra.code.i18nmod;

import net.minecraft.server.packs.FilePackResources;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;

import java.util.function.Consumer;

import static cn.elytra.code.i18nmod.I18nUpdateModUnofficial.LANGUAGE_PACK;

public class LanguagePackFinder implements RepositorySource {

	@Override
	public void loadPacks(Consumer<Pack> packConsumer, Pack.PackConstructor constructor) {
		var pack = Pack.create(
				"Minecraft-Mod-Language-Modpack-1-18.zip",
				true, () -> new FilePackResources(LANGUAGE_PACK.toFile()),
				constructor, Pack.Position.TOP, PackSource.DEFAULT
		);
		packConsumer.accept(pack);
	}
}
