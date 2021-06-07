/**
 * 
 */
package com.someguyssoftware.legacyvault.db.entity;

import java.sql.Timestamp;
import java.util.Date;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.someguyssoftware.legacyvault.inventory.VaultSlotSize;

/**
 * @author Mark Gottschling on Apr 28, 2021
 *
 */
@DatabaseTable(tableName = "accounts")
public class Account {
	
	@DatabaseField(generatedId = true, columnName = "id")
	private Integer id;

	@DatabaseField(canBeNull = false, columnName = "uuid")
	private String uuid;
	
	@DatabaseField(canBeNull = false, columnName = "mc_version")
	private String version;
	
	@DatabaseField(canBeNull = false, columnName = "game_type")
	private String gameType;
	
	@DatabaseField(canBeNull = true, columnName = "inventory", dataType=DataType.BYTE_ARRAY)
	private byte[] inventory;
	
	@DatabaseField(canBeNull = false, columnName = "max_size", defaultValue = "27")
	private Integer  maxSize = VaultSlotSize.SMALL.getSize();
	
	@DatabaseField(canBeNull = true, columnName = "locked", defaultValue = "false")
	private Boolean locked = false;
	
	@DatabaseField(columnName = "created", dataType=DataType.TIME_STAMP)
	private Timestamp created;
	
	@DatabaseField(columnName = "modified", dataType=DataType.TIME_STAMP)
	private Timestamp modified;
	
	/**
	 * 
	 */
	public Account() {
		Date d = new Date();
		this.created = new Timestamp(d.getTime());
	}
	
	public String getUuid() {
		return uuid;
	}

	public Account setUuid(String uuid) {
		this.uuid = uuid;
		return this;
	}

	public String getGameType() {
		return gameType;
	}

	public Account setGameType(String type) {
		this.gameType = type;
		return this;
	}
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public byte[] getInventory() {
		return inventory;
	}

	public void setInventory(byte[] inventory) {
		this.inventory = inventory;
	}

	@Override
	public String toString() {
		return "Account [id=" + id + ", uuid=" + uuid + ", version=" + version + ", gameType=" + gameType + "]";
	}

	public String getVersion() {
		return version;
	}

	public Account setVersion(String version) {
		this.version = version;
		return this;
	}

	public Integer getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(Integer maxSize) {
		this.maxSize = maxSize;
	}

	public Boolean getLocked() {
		return locked;
	}

	public void setLocked(Boolean locked) {
		this.locked = locked;
	}

	public Timestamp getCreated() {
		return created;
	}

	public void setCreated(Timestamp created) {
		this.created = created;
	}

	public Timestamp getModified() {
		return modified;
	}

	public void setModified(Timestamp modified) {
		this.modified = modified;
	}
}
