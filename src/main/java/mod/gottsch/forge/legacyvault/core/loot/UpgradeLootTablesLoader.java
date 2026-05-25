/*
 * This file is part of Legacy Vault.
 * Copyright (c) 2026 Mark Gottschling (gottsch)
 *
 * All rights reserved.
 *
 * Legacy Vault is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Legacy Vault is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Legacy Vault.  If not, see <http://www.gnu.org/licenses/lgpl>.
 */
package mod.gottsch.forge.legacyvault.core.loot;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import mod.gottsch.forge.legacyvault.core.LegacyVault;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.IOException;
import java.io.Reader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Loads the set of loot table IDs eligible for Vault Upgrade drops from datapack
 * JSON files at {@code data/<namespace>/upgrade_loot_tables/drops.json}.
 *
 * <p>Behaves like a Minecraft tag: each datapack contributes a file with optional
 * {@code "replace": true} (clears anything accumulated so far) and a {@code "values"}
 * array of loot table IDs. Files from all packs are processed in load order and
 * merged into a single set.
 *
 * <p>The mod ships its own copy at {@code data/legacyvault/upgrade_loot_tables/drops.json}
 * with the default adventure-chest entries; admins override or extend by placing a
 * file at the same path in a datapack.
 *
 * @author Mark Gottschling on May 24, 2026
 */
@Mod.EventBusSubscriber(modid = LegacyVault.MOD_ID)
public class UpgradeLootTablesLoader extends SimplePreparableReloadListener<Set<String>> {

    /** Resource path read on reload. Files at this path from every datapack contribute. */
    public static final ResourceLocation FILE_PATH = new ResourceLocation(LegacyVault.MOD_ID, "upgrade_loot_tables/drops.json");

    /** Currently-active set, queried by the loot modifier on every loot roll. */
    private static final Set<String> ACTIVE = ConcurrentHashMap.newKeySet();

    @SubscribeEvent
    public static void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener(new UpgradeLootTablesLoader());
    }

    public static boolean contains(String lootTableId) {
        return ACTIVE.contains(lootTableId);
    }

    public static int size() {
        return ACTIVE.size();
    }

    @Override
    protected Set<String> prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
        Set<String> accumulator = new HashSet<>();
        List<Resource> stack;
        try {
            stack = resourceManager.getResourceStack(FILE_PATH);
        } catch (Exception e) {
            LegacyVault.LOGGER.error("failed to query upgrade loot tables resource at {}", FILE_PATH, e);
            return accumulator;
        }
        for (Resource resource : stack) {
            try (Reader reader = resource.openAsReader()) {
                JsonObject json = GsonHelper.parse(reader);
                boolean replace = GsonHelper.getAsBoolean(json, "replace", false);
                if (replace) accumulator.clear();
                JsonArray values = GsonHelper.getAsJsonArray(json, "values");
                for (JsonElement v : values) {
                    accumulator.add(v.getAsString());
                }
            } catch (IOException e) {
                LegacyVault.LOGGER.error("failed to read upgrade loot tables file from pack {}", resource.sourcePackId(), e);
            } catch (Exception e) {
                LegacyVault.LOGGER.error("malformed upgrade loot tables JSON in pack {}", resource.sourcePackId(), e);
            }
        }
        return accumulator;
    }

    @Override
    protected void apply(Set<String> data, ResourceManager resourceManager, ProfilerFiller profiler) {
        ACTIVE.clear();
        ACTIVE.addAll(data);
        LegacyVault.LOGGER.info("loaded {} vault upgrade loot table entries", ACTIVE.size());
    }
}
