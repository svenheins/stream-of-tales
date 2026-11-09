/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.main;

/** influence type only concerns PlayerEntities (Players, Agents (Human and animals)) */
public enum AttributeType {
	LIFE, MAXLIFE, MANA, MAXMANA, SPEED, MAXSPEED, ARMOR, BLOCKCHANCE, STRENGH, 
	LIFEREGENERATION, MANAREGENERATION, ATTACKSPEED, 
	LIGHTRADIUS, LIFEVISIONRADIUS, GOLDVISIONRADIUS,
	VISIBILITY, ANIMALCAMOUFLAGE, HUMANCAMOUFLAGE,
	WILLPOWER, MX, MY, ATTACKDISTANCE, ATTACKSQUARESIZE, SENDMX, SENDMY, LIFELEECH,
	CRAFTLEVEL, ENERGY, MAXENERGY, ENERGYREGENERATION,
	INTERACTWOOD, INTERACTSTONE, INTERACTSOIL
}
