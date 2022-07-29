package de.svenheins.objects;

public class Tile {
	private int id;
	private boolean ul;
	private boolean ur;
	private boolean dl;
	private boolean dr;
	
	public Tile(int id, boolean ul, boolean ur, boolean dl, boolean dr) {
		this.dl = dl;
		this.dr = dr;
		this.ul = ul;
		this.ur = ur;
		this.setId(id);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean hasUl() {
		return ul;
	}

	public void setUl(boolean ul) {
		this.ul = ul;
	}

	public boolean hasUr() {
		return ur;
	}

	public void setUr(boolean ur) {
		this.ur = ur;
	}

	public boolean hasDl() {
		return dl;
	}

	public void setDl(boolean dl) {
		this.dl = dl;
	}

	public boolean hasDr() {
		return dr;
	}

	public void setDr(boolean dr) {
		this.dr = dr;
	}
	
	
	public void setIdByCorners() {
		if (ul && !ur && !dr && !dl) setId(79);
		else if (ul && ur && !dr && !dl) setId(78);
		else if (!ul && ur && !dr && !dl) setId(77);
		else if (ul && !ur && !dr && dl) setId(63);
		else if (ul && ur && dr && dl) setId(62);
		else if (!ul && ur && dr && !dl) setId(61);
		else if (!ul && !ur && !dr && dl) setId(47);
		else if (!ul && !ur && dr && dl) setId(46);
		else if (!ul && !ur && dr && !dl) setId(45);
		else if (ul && !ur && dr && dl) setId(60);
		else if (!ul && ur && dr && dl) setId(59);
		else if (ul && ur && !dr && dl) setId(76);
		else if (ul && ur && dr && !dl) setId(75);
		else if (!ul && ur && !dr && dl) setId(156);
		else if (ul && !ur && dr && !dl) setId(155);
		else setId(0);
	}
	
}
