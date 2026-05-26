# Changelog for Forge 1.20.1 - Legacy Vault

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [3.0.0] - 2026-05-24

### 🐛 Fixed

- **XLarge vaults could silently lose items when you closed them** — the vault was
  only tracking 84 slots internally instead of the full 91, so items in the last 7
  slots could disappear without warning. Fixed, and a safety check was added so
  this can never happen again even if the numbers drift.
- **Community vault blacklist wasn't blocking anyone** — players listed on the blacklist
  could still open community vaults as if they weren't blocked at all. Fixed.
- **Game could crash if the vault block went missing** — several places assumed the
  vault block was always present and would throw an error if it wasn't (for example,
  if another mod removed or replaced it). Those spots now handle a missing block
  gracefully.
- **Opening and immediately closing a vault triggered an unnecessary save** — every
  time you opened a vault, it would write your inventory to disk even if you didn't
  touch anything. Now it only saves when you actually move items.
- **Vault saves are now protected against crashes mid-write** — your inventory is
  written to a temporary file first, then swapped in atomically, so a crash or power
  cut during a save can't leave your data in a broken state.
- **An invalid filter pattern in the config no longer crashes the server** — if a
  bad entry slips into the item whitelist or blacklist, Legacy Vault now logs a clear
  warning and skips that entry instead of crashing on startup.
- **Placing a vault manually never tracked its location** — the `/legacyvault locations`
  command was only showing vaults placed by admins using the spawn command, not ones
  you placed yourself from your inventory. Fixed — all vault placements are now tracked
  correctly regardless of how they were placed.
- **Breaking a vault in creative mode didn't update the vault count or location list**
  — destroying a vault in creative mode now cleans up correctly, the same as survival.
- **Stale vault locations are cleaned up on login** — if a vault was destroyed while
  you were offline (by an admin, explosion, etc.), the leftover location entry and
  vault count are now corrected automatically when you next log in.
- **"Inventory" label was missing from vault screens** — both the personal and
  community vault screens were not showing the standard "Inventory" text above
  your hotbar rows like other containers do. Added.
- **"Vaults Remaining" text was clipped at the bottom of the screen** — the vault
  GUI panel was a few pixels too short, cutting off the bottom of the text. Panel
  height was extended so the text now has comfortable padding below it.

### ✨ Added

- **Vault Expansion Upgrades** — vaults now grow with you. New players start with a
  single row of 9 slots and can unlock more rows over time, up to 10 rows (90 slots).
  Both the starting size and the maximum are configurable per server.
- **Vault Upgrade item** — right-click your placed vault while holding a Vault Upgrade
  to unlock the next row. Consumed on use (unless you're in creative). Shows a message
  if you're already at the maximum.
- **Scrollable vault screen** — the vault GUI now has a scrollbar so you can see all
  your rows. Locked rows show a clear "X" overlay so you can tell at a glance which
  rows you haven't unlocked yet.
- **Search bar inside the vault** — type to dim items that don't match. Useful when
  your vault gets full.
- **Sort button (Az)** — one click sorts your active rows alphabetically. Locked rows
  stay untouched.
- **New admin commands** — all under `/legacyvault`, require permission level 2:
  - `/legacyvault inspect <player>` — shows a player's vault contents and slot usage.
  - `/legacyvault reload <player>` — reloads a player's vault from disk (useful after
    manual file edits).
  - `/legacyvault clear <player>` — permanently wipes all items from a player's vault.
    Asks for confirmation before deleting anything.
  - `/legacyvault transfer <from> <to>` — moves everything from one player's vault to
    another. The source vault is emptied after the transfer.
  - `/legacyvault locations <player>` — lists the coordinates of all of a player's
    placed vaults, including which dimension they're in.
  - `/legacyvault locations clear <player>` — clears a player's vault location list.
  - `/legacyvault count <targets> [value]` — shows or sets a player's vault count.
  - `/legacyvault spawn <pos> [targets] [type] [direction]` — places a vault in the
    world. Type can be `rustic`, `classic`, or `community`. Defaults to rustic facing
    north if not specified.
  - `/legacyvault tier inspect <player>` — shows the player's current tier, active
    slot count, and the configured starting/max tiers.
  - `/legacyvault tier set_tier <targets> <value>` — set a player's tier directly.
    Clamped to the configured maximum.
  - `/legacyvault tier reset <targets>` — set a player back to the configured starting tier.
  - `/legacyvault tier max <targets>` — set a player to the configured maximum tier.
- **Item-tag whitelist and blacklist** — admins can now use datapack item tags
  (`legacyvault:vault_items_whitelist` and `legacyvault:vault_items_blacklist`) to
  control what can go in vaults. Tags are checked first; the config-based regex lists
  still work as a fallback. The blacklist is also enforced when shift-clicking, so
  there's no sneaking past it.
- **Treasure2 chest blacklist out of the box** — if you have Treasure2 installed,
  its various chest blocks are now blocked from being stored in a vault by default
  (so they can't be used to cheat past the slot limit).
- **More popular storage mods blocked by default** — chests, barrels, shulker boxes,
  and backpacks from Iron Chests, Sophisticated Backpacks, Sophisticated Storage, and
  Traveler's Backpack are now blocked from being stored inside a vault, alongside
  the Treasure2 blocks. Same reason: they'd let you cheat past the slot limit.
- **Per-world tier reset (optional)** — a new `resetTierPerWorld` setting lets each
  world track its own vault tier separately. Your items still carry over (that's
  the whole point of the mod), but you'll start each new world at the configured
  starting tier and have to earn your way back up. Turning the option off later
  restores the best tier you'd ever reached across any world. Off by default.
- **Item count tooltip** — hovering over an item already in your vault now shows
  "× N in vault" if you have more of the same item in other slots. Helpful for
  spotting duplicates and managing stacks.
- **Community vault is now fully featured** — the community vault previously had
  a stripped-down interface. It now matches the personal vault feature-for-feature:
  expansion tiers, scrollable rows, search bar, sort button, locked-slot indicators,
  and the item count tooltip. Tier is shared with your personal vault (upgrade once,
  it applies to both).
- **Vault encryption (optional)** — turn on the new `encryptVaultData` setting
  and your vault files are scrambled on disk so no one can read them without
  the server's key. Protects against people sneaking copies of vault files off
  the server or pretending to be another player. The first time you turn it on,
  the server creates a key file at `config/legacyvault/legacyvault-secret.dat` —
  back this up, because if you lose it, the encrypted vaults can't be read
  anymore. Off by default. Safe to turn on and off whenever you want: old files
  keep working either way.
- **`/legacyvault encryption` command** — admins (permission level 2) can turn
  vault encryption on or off without editing the config file or restarting the
  server. Takes effect immediately for the next vault save.
  - `/legacyvault encryption status` — shows whether encryption is on or off.
  - `/legacyvault encryption on` — turns it on.
  - `/legacyvault encryption off` — turns it off.
- **Vault Upgrades can drop from loot chests** — Vault Upgrade items now have a
  small chance to appear in adventure chest loot: simple dungeons, mineshafts,
  strongholds, jungle/desert temples, woodland mansions, end city treasure,
  nether bridge, bastion treasure, and ancient cities. Defaults: 2% chance per
  matching chest, 1 upgrade per drop. Server admins can change the chance and
  count, restrict which loot tables it applies to (via a datapack-overridable
  list at `data/legacyvault/upgrade_loot_tables/drops.json`), or turn the whole
  feature off if they'd rather hand out upgrades only as quest/event rewards.

### ⚙️ Changed

- **Vault tier follows your items across worlds.** Vault items have always been
  saved outside the world (so they follow you), and now your tier does too. Reach
  tier 5 in one world and your tier-5 vault is waiting for you in any other world.
- **Legacy vaults are honored.** If you had a full XLarge vault from a previous
  version, you're carried over at the maximum tier so all 90 slots stay accessible
  on first login.
- **Vault lid animation feels smoother** — the lid open/close motion now eases in
  AND out instead of snapping at the start, for a more polished feel. Applied to
  all three vault models (classic, rustic, community).

### 🐛 Also Fixed

- The vault items whitelist tag (`legacyvault:vault_items_whitelist`) didn't exist
  in the datapack until now; Minecraft logged a "missing tag" warning at startup.

---

## [2.0.1] - 2025-10-07

### Changed

- fixed not being able to break Personal Vaults when Community Config option is enabled.

## [2.0.0] - 2025-02-24
## -Breaking Version Change-

### Changed
- **replaced DB storage with external individual player NBT storage!**
  - this prevents loss of all data due to db corruption, eliminates db security issues,
  and removes introduction and reliance on extraneous 3rd party libraries (which may have
  their own security issues).
- refactored Config properties.
- allow both Personal and Community (aka Public) vaults, and the ability to enable/disable both independently.
- replaced Vault Application item texture.
- renamed Vault Application to Vault Contract .
- separated the dual role/purpose of the Vault. ie a Vault no longer is either Personal or Public - individual Block/Chests were added for each.  
- the original Legacy Vault is renamed to Classic Vault.
- the recipes to craft the Classic/Original Vault now uses a Barrel instead of a Chest.
- fixed the quick move stack action for the vaults.

### Added
- Rustic Vault - a personal vault.
- Community Vault - a community vault.
- set of recipes for Rustic Vault that uses a Chest (the recipes that was previously used for the Classic/Original vault).

## [1.4.0] - 2024-12-19

### Changed

- Move DB config properties from Server to Common.