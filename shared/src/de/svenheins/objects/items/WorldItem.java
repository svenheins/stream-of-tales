package de.svenheins.objects.items;

import java.math.BigInteger;

import de.svenheins.objects.Entity;

public class WorldItem {
	private BigInteger id;
	private Entity itemEntity;
	private Item item;
	private long creationTime;
	
	
	public WorldItem(BigInteger id, Item item, Entity itemEntity) {
		this.id = id;
		item.setId(id);
		this.setItem(item);
		this.setItemEntity(itemEntity);
		this.setCreationTime(System.currentTimeMillis());
	}

	public Entity getItemEntity() {
		return itemEntity;
	}

	public void setItemEntity(Entity itemEntity) {
		this.itemEntity = itemEntity;
	}

	public Item getItem() {
		return item;
	}

	public void setItem(Item item) {
		this.item = item;
	}

	public long getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(long creationTime) {
		this.creationTime = creationTime;
	}

	public BigInteger getId() {
		return id;
	}

	public void setId(BigInteger id) {
		this.id = id;
	}
}
