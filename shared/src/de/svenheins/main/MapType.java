package de.svenheins.main;

/** MapType determines the connection location to other maps */
public enum MapType {
	ZERO, 	// 0+1 = 1
	U,		// 1+1 = 2
	R,		// 2 3
	D,		// 3 4
	L,		// 4 5
	UR,		// 5 6
	RD,		// 6 7
	LD,		// 7 8
	LU,		// 8 9
	UD,		// 9 10
	LR,		// 10 11
	URD,	// 11 12
	LRD,	// 12 13
	LUD,	// 13 14
	LUR,	// 14 15
	LURD	// 15+ 1 = 16
}
