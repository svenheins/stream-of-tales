/* Copyright (C) Sven Heins - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * author: Sven Heins
 * date: 05.07.2015
 */
package de.svenheins.objects.items;

public interface Stackable {

	public void add();
	public void add(int count);
	public void remove();
	public void remove(int count);
}
