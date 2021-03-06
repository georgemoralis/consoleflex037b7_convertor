/******************************************************************************
	KIM-1

	machine Driver

	Juergen Buchmueller, Jan 2000

******************************************************************************/
/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */ 
package machine;

public class kim1
{
	
	typedef struct
	{
		UINT8 dria; 	/* Data register A input */
		UINT8 droa; 	/* Data register A output */
		UINT8 ddra; 	/* Data direction register A; 1 bits = output */
		UINT8 drib; 	/* Data register B input */
		UINT8 drob; 	/* Data register B output */
		UINT8 ddrb; 	/* Data direction register B; 1 bits = output */
		UINT8 irqen;	/* IRQ enabled ? */
		UINT8 state;	/* current timer state (bit 7) */
		double clock;	/* 100000/1(,8,64,1024) */
		void *timer;	/* timer callback */
	}
	M6530;
	
	static M6530 m6530[2];
	
	
	public static InitDriverPtr init_kim1 = new InitDriverPtr() { public void handler() 
	{
		UINT8 *dst;
		int x, y, i;
	
		static char *seg7 =
		"....aaaaaaaaaaaaa." \
		"...f.aaaaaaaaaaa.b" \
		"...ff.aaaaaaaaa.bb" \
		"...fff.........bbb" \
		"...fff.........bbb" \
		"...fff.........bbb" \
		"..fff.........bbb." \
		"..fff.........bbb." \
		"..fff.........bbb." \
		"..ff...........bb." \
		"..f.ggggggggggg.b." \
		"..gggggggggggggg.." \
		".e.ggggggggggg.c.." \
		".ee...........cc.." \
		".eee.........ccc.." \
		".eee.........ccc.." \
		".eee.........ccc.." \
		"eee.........ccc..." \
		"eee.........ccc..." \
		"eee.........ccc..." \
		"ee.ddddddddd.cc..." \
		"e.ddddddddddd.c..." \
		".ddddddddddddd...." \
		"..................";
	
	
		static char *keys[24] =
		{
			"aaaaaaaaaaaaaaaaaaaaaaaa" \
			"aaaaaaaaaaaaaaaaaaaaaaaa" \
			"aaaaaaaaaaccccaaaaaaaaaa" \
			"aaaaaaaaccccccccaaaaaaaa" \
			"aaaaaaaaccaaaaccaaaaaaaa" \
			"aaaaaaaccaaaaaaccaaaaaaa" \
			"aaaaaaaccaaaaacccaaaaaaa" \
			"aaaaaaaccaaaaccccaaaaaaa" \
			"aaaaaaaccaaaccaccaaaaaaa" \
			"aaaaaaaccaaccaaccaaaaaaa" \
			"aaaaaaaccaccaaaccaaaaaaa" \
			"aaaaaaaccccaaaaccaaaaaaa" \
			"aaaaaaacccaaaaaccaaaaaaa" \
			"aaaaaaaaccaaaaccaaaaaaaa" \
			"aaaaaaaaccccccccaaaaaaaa" \
			"aaaaaaaaaaccccaaaaaaaaaa" \
			"aaaaaaaaaaaaaaaaaaaaaaaa" \
			"aaaaaaaaaaaaaaaaaaaaaaaa",
	
			"aaaaaaaaaaaaaaaaaaaaaaaa" \
			"aaaaaaaaaaaaaaaaaaaaaaaa" \
			"aaaaaaaaaaaccaaaaaaaaaaa" \
			"aaaaaaaaaacccaaaaaaaaaaa" \
			"aaaaaaaaaccccaaaaaaaaaaa" \
			"aaaaaaaaccaccaaaaaaaaaaa" \
			"aaaaaaaaaaaccaaaaaaaaaaa" \
			"aaaaaaaaaaaccaaaaaaaaaaa" \
			"aaaaaaaaaaaccaaaaaaaaaaa" \
			"aaaaaaaaaaaccaaaaaaaaaaa" \
			"aaaaaaaaaaaccaaaaaaaaaaa" \
			"aaaaaaaaaaaccaaaaaaaaaaa" \
			"aaaaaaaaaaaccaaaaaaaaaaa" \
			"aaaaaaaaaaaccaaaaaaaaaaa" \
			"aaaaaaaaaaaccaaaaaaaaaaa" \
			"aaaaaaaaaaaccaaaaaaaaaaa" \
			"aaaaaaaaaaaaaaaaaaaaaaaa" \
			"aaaaaaaaaaaaaaaaaaaaaaaa",
	
			"aaaaaaaaaaaaaaaaaaaaaaaa" \
			"aaaaaaaaaaaaaaaaaaaaaaaa" \
			"aaaaaaaaccccccccaaaaaaaa" \
			"aaaaaaaccccccccccaaaaaaa" \
			"aaaaaaaccaaaaaaccaaaaaaa" \
			"aaaaaaaaaaaaaaaccaaaaaaa" \
			"aaaaaaaaaaaaaacccaaaaaaa" \
			"aaaaaaaaaaaaccccaaaaaaaa" \
			"aaaaaaaaaccccccaaaaaaaaa" \
			"aaaaaaaaccccaaaaaaaaaaaa" \
			"aaaaaaacccaaaaaaaaaaaaaa" \
			"aaaaaaaccaaaaaaaaaaaaaaa" \
			"aaaaaaaccaaaaaaaaaaaaaaa" \
			"aaaaaaaccaaaaaaaaaaaaaaa" \
			"aaaaaaaccccccccccaaaaaaa" \
			"aaaaaaaccccccccccaaaaaaa" \
			"aaaaaaaaaaaaaaaaaaaaaaaa" \
			"aaaaaaaaaaaaaaaaaaaaaaaa",
	
			"aaaaaaaaaaaaaaaaaaaaaaaa" \
			"aaaaaaaaaaaaaaaaaaaaaaaa" \
			"aaaaaaaaaccccccaaaaaaaaa" \
			"aaaaaaaaccccccccaaaaaaaa" \
			"aaaaaaacccaaaacccaaaaaaa" \
			"aaaaaaaccaaaaaaccaaaaaaa" \
			"aaaaaaaaaaaaaaaccaaaaaaa" \
			"aaaaaaaaaaaaaacccaaaaaaa" \
			"aaaaaaaaaaccccccaaaaaaaa" \
			"aaaaaaaaaaccccccaaaaaaaa" \
			"aaaaaaaaaaaaaacccaaaaaaa" \
			"aaaaaaaaaaaaaaaccaaaaaaa" \
			"aaaaaaaccaaaaaaccaaaaaaa" \
			"aaaaaaacccaaaacccaaaaaaa" \
			"aaaaaaaaccccccccaaaaaaaa" \
			"aaaaaaaaaccccccaaaaaaaaa" \
			"aaaaaaaaaaaaaaaaaaaaaaaa" \
			"aaaaaaaaaaaaaaaaaaaaaaaa",
	
			"aaaaaaaaaaaaaaaaaaaaaaaa" \
			"aaaaaaaaaaaaaaaaaaaaaaaa" \
			"aaaaaaaaaaaaaacaaaaaaaaa" \
			"aaaaaaaaaaaaaccaaaaaaaaa" \
			"aaaaaaaaaaaacccaaaaaaaaa" \
			"aaaaaaaaaaaccccaaaaaaaaa" \
			"aaaaaaaaaaccaccaaaaaaaaa" \
			"aaaaaaaaaccaaccaaaaaaaaa" \
			"aaaaaaaaccaaaccaaaaaaaaa" \
			"aaaaaaaccaaaaccaaaaaaaaa" \
			"aaaaaacccccccccccaaaaaaa" \
			"aaaaaacccccccccccaaaaaaa" \
			"aaaaaaaaaaaaaccaaaaaaaaa" \
			"aaaaaaaaaaaaaccaaaaaaaaa" \
			"aaaaaaaaaaaaaccaaaaaaaaa" \
			"aaaaaaaaaaaaaccaaaaaaaaa" \
			"aaaaaaaaaaaaaaaaaaaaaaaa" \
			"aaaaaaaaaaaaaaaaaaaaaaaa",
	
			"aaaaaaaaaaaaaaaaaaaaaaaa" \
			"aaaaaaaaaaaaaaaaaaaaaaaa" \
			"aaaaaaaccccccccccaaaaaaa" \
			"aaaaaaaccccccccccaaaaaaa" \
			"aaaaaaaccaaaaaaaaaaaaaaa" \
			"aaaaaaaccaaaaaaaaaaaaaaa" \
			"aaaaaaaccccccccaaaaaaaaa" \
			"aaaaaaacccccccccaaaaaaaa" \
			"aaaaaaaaaaaaaacccaaaaaaa" \
			"aaaaaaaaaaaaaaaccaaaaaaa" \
			"aaaaaaaaaaaaaaaccaaaaaaa" \
			"aaaaaaaaaaaaaaaccaaaaaaa" \
			"aaaaaaaccaaaaaaccaaaaaaa" \
			"aaaaaaacccaaaacccaaaaaaa" \
			"aaaaaaaaccccccccaaaaaaaa" \
			"aaaaaaaaaccccccaaaaaaaaa" \
			"aaaaaaaaaaaaaaaaaaaaaaaa" \
			"aaaaaaaaaaaaaaaaaaaaaaaa",
	
			"aaaaaaaaaaaaaaaaaaaaaaaa" \
			"aaaaaaaaaaaaaaaaaaaaaaaa" \
			"aaaaaaaaaccccccaaaaaaaaa" \
			"aaaaaaaaccccccccaaaaaaaa" \
			"aaaaaaacccaaaacccaaaaaaa" \
			"aaaaaaaccaaaaaaaaaaaaaaa" \
			"aaaaaaaccaaaaaaaaaaaaaaa" \
			"aaaaaaaccaccccaaaaaaaaaa" \
			"aaaaaaacccccccccaaaaaaaa" \
			"aaaaaaaccccaaaccaaaaaaaa" \
			"aaaaaaaccaaaaaaccaaaaaaa" \
			"aaaaaaaccaaaaaaccaaaaaaa" \
			"aaaaaaaccaaaaaaccaaaaaaa" \
			"aaaaaaacccaaaacccaaaaaaa" \
			"aaaaaaaaccccccccaaaaaaaa" \
			"aaaaaaaaaccccccaaaaaaaaa" \
			"aaaaaaaaaaaaaaaaaaaaaaaa" \
			"aaaaaaaaaaaaaaaaaaaaaaaa",
	
			"aaaaaaaaaaaaaaaaaaaaaaaa" \
			"aaaaaaaaaaaaaaaaaaaaaaaa" \
			"aaaaaaaccccccccccaaaaaaa" \
			"aaaaaaaccccccccccaaaaaaa" \
			"aaaaaaaaaaaaaaaccaaaaaaa" \
			"aaaaaaaaaaaaaacccaaaaaaa" \
			"aaaaaaaaaaaaacccaaaaaaaa" \
			"aaaaaaaaaaaacccaaaaaaaaa" \
			"aaaaaaaaaaacccaaaaaaaaaa" \
			"aaaaaaaaaacccaaaaaaaaaaa" \
			"aaaaaaaaacccaaaaaaaaaaaa" \
			"aaaaaaaacccaaaaaaaaaaaaa" \
			"aaaaaaacccaaaaaaaaaaaaaa" \
			"aaaaaacccaaaaaaaaaaaaaaa" \
			"aaaaaacccaaaaaaaaaaaaaaa" \
			"aaaaaaccaaaaaaaaaaaaaaaa" \
			"aaaaaaaaaaaaaaaaaaaaaaaa" \
			"aaaaaaaaaaaaaaaaaaaaaaaa",
	
			"aaaaaaaaaaaaaaaaaaaaaaaa" \
			"aaaaaaaaaaaaaaaaaaaaaaaa" \
			"aaaaaaaaaccccccaaaaaaaaa" \
			"aaaaaaaaccccccccaaaaaaaa" \
			"aaaaaaacccaaaacccaaaaaaa" \
			"aaaaaaaccaaaaaaccaaaaaaa" \
			"aaaaaaaccaaaaaaccaaaaaaa" \
			"aaaaaaaaccaaaaccaaaaaaaa" \
			"aaaaaaaaaccccccaaaaaaaaa" \
			"aaaaaaaaccccccccaaaaaaaa" \
			"aaaaaaaccaaaaaaccaaaaaaa" \
			"aaaaaaaccaaaaaaccaaaaaaa" \
			"aaaaaaaccaaaaaaccaaaaaaa" \
			"aaaaaaacccaaaacccaaaaaaa" \
			"aaaaaaaaccccccccaaaaaaaa" \
			"aaaaaaaaaccccccaaaaaaaaa" \
			"aaaaaaaaaaaaaaaaaaaaaaaa" \
			"aaaaaaaaaaaaaaaaaaaaaaaa",
	
			"aaaaaaaaaaaaaaaaaaaaaaaa" \
			"aaaaaaaaaaaaaaaaaaaaaaaa" \
			"aaaaaaaaaccccccaaaaaaaaa" \
			"aaaaaaaaccccccccaaaaaaaa" \
			"aaaaaaacccaaaacccaaaaaaa" \
			"aaaaaaaccaaaaaaccaaaaaaa" \
			"aaaaaaaccaaaaaaccaaaaaaa" \
			"aaaaaaaccaaaaaaccaaaaaaa" \
			"aaaaaaaacccaaacccaaaaaaa" \
			"aaaaaaaacccccccccaaaaaaa" \
			"aaaaaaaaaaccccaccaaaaaaa" \
			"aaaaaaaaaaaaaaaccaaaaaaa" \
			"aaaaaaaaaaaaaaaccaaaaaaa" \
			"aaaaaaacccaaaacccaaaaaaa" \
			"aaaaaaaaccccccccaaaaaaaa" \
			"aaaaaaaaaccccccaaaaaaaaa" \
			"aaaaaaaaaaaaaaaaaaaaaaaa" \
			"aaaaaaaaaaaaaaaaaaaaaaaa",
	
			"aaaaaaaaaaaaaaaaaaaaaaaa" \
			"aaaaaaaaaaaaaaaaaaaaaaaa" \
			"aaaaaaaaaaaccaaaaaaaaaaa" \
			"aaaaaaaaaaccccaaaaaaaaaa" \
			"aaaaaaaaaaccccaaaaaaaaaa" \
			"aaaaaaaaaccccccaaaaaaaaa" \
			"aaaaaaaaaccaaccaaaaaaaaa" \
			"aaaaaaaaaccaaccaaaaaaaaa" \
			"aaaaaaaaccccccccaaaaaaaa" \
			"aaaaaaaaccccccccaaaaaaaa" \
			"aaaaaaaaccaaaaccaaaaaaaa" \
			"aaaaaaacccaaaacccaaaaaaa" \
			"aaaaaaaccaaaaaaccaaaaaaa" \
			"aaaaaaaccaaaaaaccaaaaaaa" \
			"aaaaaacccaaaaaacccaaaaaa" \
			"aaaaaaccaaaaaaaaccaaaaaa" \
			"aaaaaaaaaaaaaaaaaaaaaaaa" \
			"aaaaaaaaaaaaaaaaaaaaaaaa",
	
			"aaaaaaaaaaaaaaaaaaaaaaaa" \
			"aaaaaaaaaaaaaaaaaaaaaaaa" \
			"aaaaaaaccccccccaaaaaaaaa" \
			"aaaaaaacccccccccaaaaaaaa" \
			"aaaaaaaccaaaaacccaaaaaaa" \
			"aaaaaaaccaaaaaaccaaaaaaa" \
			"aaaaaaaccaaaaaaccaaaaaaa" \
			"aaaaaaaccaaaaaccaaaaaaaa" \
			"aaaaaaaccccccccaaaaaaaaa" \
			"aaaaaaacccccccccaaaaaaaa" \
			"aaaaaaaccaaaaacccaaaaaaa" \
			"aaaaaaaccaaaaaaccaaaaaaa" \
			"aaaaaaaccaaaaaaccaaaaaaa" \
			"aaaaaaaccaaaaacccaaaaaaa" \
			"aaaaaaacccccccccaaaaaaaa" \
			"aaaaaaaccccccccaaaaaaaaa" \
			"aaaaaaaaaaaaaaaaaaaaaaaa" \
			"aaaaaaaaaaaaaaaaaaaaaaaa",
	
			"aaaaaaaaaaaaaaaaaaaaaaaa" \
			"aaaaaaaaaaaaaaaaaaaaaaaa" \
			"aaaaaaaaccccccccaaaaaaaa" \
			"aaaaaaaccccccccccaaaaaaa" \
			"aaaaaaaccaaaaaaccaaaaaaa" \
			"aaaaaaaccaaaaaaaaaaaaaaa" \
			"aaaaaaaccaaaaaaaaaaaaaaa" \
			"aaaaaaaccaaaaaaaaaaaaaaa" \
			"aaaaaaaccaaaaaaaaaaaaaaa" \
			"aaaaaaaccaaaaaaaaaaaaaaa" \
			"aaaaaaaccaaaaaaaaaaaaaaa" \
			"aaaaaaaccaaaaaaaaaaaaaaa" \
			"aaaaaaaccaaaaaaaaaaaaaaa" \
			"aaaaaaaccaaaaaaccaaaaaaa" \
			"aaaaaaaccccccccccaaaaaaa" \
			"aaaaaaaaccccccccaaaaaaaa" \
			"aaaaaaaaaaaaaaaaaaaaaaaa" \
			"aaaaaaaaaaaaaaaaaaaaaaaa",
	
			"aaaaaaaaaaaaaaaaaaaaaaaa" \
			"aaaaaaaaaaaaaaaaaaaaaaaa" \
			"aaaaaaacccccccaaaaaaaaaa" \
			"aaaaaaaccccccccaaaaaaaaa" \
			"aaaaaaaccaaaacccaaaaaaaa" \
			"aaaaaaaccaaaaaccaaaaaaaa" \
			"aaaaaaaccaaaaacccaaaaaaa" \
			"aaaaaaaccaaaaaaccaaaaaaa" \
			"aaaaaaaccaaaaaaccaaaaaaa" \
			"aaaaaaaccaaaaaaccaaaaaaa" \
			"aaaaaaaccaaaaaaccaaaaaaa" \
			"aaaaaaaccaaaaacccaaaaaaa" \
			"aaaaaaaccaaaaaccaaaaaaaa" \
			"aaaaaaaccaaaacccaaaaaaaa" \
			"aaaaaaaccccccccaaaaaaaaa" \
			"aaaaaaacccccccaaaaaaaaaa" \
			"aaaaaaaaaaaaaaaaaaaaaaaa" \
			"aaaaaaaaaaaaaaaaaaaaaaaa",
	
			"aaaaaaaaaaaaaaaaaaaaaaaa" \
			"aaaaaaaaaaaaaaaaaaaaaaaa" \
			"aaaaaaaccccccccccaaaaaaa" \
			"aaaaaaaccccccccccaaaaaaa" \
			"aaaaaaaccaaaaaaaaaaaaaaa" \
			"aaaaaaaccaaaaaaaaaaaaaaa" \
			"aaaaaaaccaaaaaaaaaaaaaaa" \
			"aaaaaaaccaaaaaaaaaaaaaaa" \
			"aaaaaaaccccccaaaaaaaaaaa" \
			"aaaaaaaccccccaaaaaaaaaaa" \
			"aaaaaaaccaaaaaaaaaaaaaaa" \
			"aaaaaaaccaaaaaaaaaaaaaaa" \
			"aaaaaaaccaaaaaaaaaaaaaaa" \
			"aaaaaaaccaaaaaaaaaaaaaaa" \
			"aaaaaaaccccccccccaaaaaaa" \
			"aaaaaaaccccccccccaaaaaaa" \
			"aaaaaaaaaaaaaaaaaaaaaaaa" \
			"aaaaaaaaaaaaaaaaaaaaaaaa",
	
			"aaaaaaaaaaaaaaaaaaaaaaaa" \
			"aaaaaaaaaaaaaaaaaaaaaaaa" \
			"aaaaaaaccccccccccaaaaaaa" \
			"aaaaaaaccccccccccaaaaaaa" \
			"aaaaaaaccaaaaaaaaaaaaaaa" \
			"aaaaaaaccaaaaaaaaaaaaaaa" \
			"aaaaaaaccaaaaaaaaaaaaaaa" \
			"aaaaaaaccaaaaaaaaaaaaaaa" \
			"aaaaaaaccccccaaaaaaaaaaa" \
			"aaaaaaaccccccaaaaaaaaaaa" \
			"aaaaaaaccaaaaaaaaaaaaaaa" \
			"aaaaaaaccaaaaaaaaaaaaaaa" \
			"aaaaaaaccaaaaaaaaaaaaaaa" \
			"aaaaaaaccaaaaaaaaaaaaaaa" \
			"aaaaaaaccaaaaaaaaaaaaaaa" \
			"aaaaaaaccaaaaaaaaaaaaaaa" \
			"aaaaaaaaaaaaaaaaaaaaaaaa" \
			"aaaaaaaaaaaaaaaaaaaaaaaa",
	
			"aaaaaaaaaaaaaaaaaaaaaaaa" \
			"aaaaaaaaaaaaaaaaaaaaaaaa" \
			"aaaaaaccaaaaacccccaaaaaa" \
			"aaaaaaccaaaaaccccccaaaaa" \
			"aaaaaccccaaaaccaacccaaaa" \
			"aaaaaccccaaaaccaaaccaaaa" \
			"aaaaccccccaaaccaaaaccaaa" \
			"aaaaccaaccaaaccaaaaccaaa" \
			"aaaaccccccaaaccaaaaccaaa" \
			"aaaccccccccaaccaaaaccaaa" \
			"aaacccaacccaaccaaaaccaaa" \
			"aaaccaaaaccaaccaaaaccaaa" \
			"aaaccaaaaccaaccaaaccaaaa" \
			"aacccaaaacccaccaacccaaaa" \
			"aaccaaaaaaccaccccccaaaaa" \
			"aaccaaaaaaccacccccaaaaaa" \
			"aaaaaaaaaaaaaaaaaaaaaaaa" \
			"aaaaaaaaaaaaaaaaaaaaaaaa",
	
			"aaaaaaaaaaaaaaaaaaaaaaaa" \
			"aaaaaaaaaaaaaaaaaaaaaaaa" \
			"aaacccccaaaaaaaccaaaaaaa" \
			"aaaccccccaaaaaaccaaaaaaa" \
			"aaaccaacccaaaaccccaaaaaa" \
			"aaaccaaaccaaaaccccaaaaaa" \
			"aaaccaaaaccaaccccccaaaaa" \
			"aaaccaaaaccaaccaaccaaaaa" \
			"aaaccaaaaccaaccccccaaaaa" \
			"aaaccaaaaccaccccccccaaaa" \
			"aaaccaaaaccacccaacccaaaa" \
			"aaaccaaaaccaccaaaaccaaaa" \
			"aaaccaaaccaaccaaaaccaaaa" \
			"aaaccaacccacccaaaacccaaa" \
			"aaaccccccaaccaaaaaaccaaa" \
			"aaacccccaaaccaaaaaaccaaa" \
			"aaaaaaaaaaaaaaaaaaaaaaaa" \
			"aaaaaaaaaaaaaaaaaaaaaaaa",
	
			"aaaaaaaaaaaaaaaaaaaaaaaa" \
			"aaaaaaaaaaaaaaaaaaaaaaaa" \
			"aaaaaaaaaaaccaaaaaaaaaaa" \
			"aaaaaaaaaaaccaaaaaaaaaaa" \
			"aaaaaaaaaaaccaaaaaaaaaaa" \
			"aaaaaaaaaaaccaaaaaaaaaaa" \
			"aaaaaaaaaaaccaaaaaaaaaaa" \
			"aaaaaaaaaaaccaaaaaaaaaaa" \
			"aaaaaccccccccccccccaaaaa" \
			"aaaaaccccccccccccccaaaaa" \
			"aaaaaaaaaaaccaaaaaaaaaaa" \
			"aaaaaaaaaaaccaaaaaaaaaaa" \
			"aaaaaaaaaaaccaaaaaaaaaaa" \
			"aaaaaaaaaaaccaaaaaaaaaaa" \
			"aaaaaaaaaaaccaaaaaaaaaaa" \
			"aaaaaaaaaaaccaaaaaaaaaaa" \
			"aaaaaaaaaaaaaaaaaaaaaaaa" \
			"aaaaaaaaaaaaaaaaaaaaaaaa",
	
			"aaaaaaaaaaaaaaaaaaaaaaaa" \
			"aaaaaaaaaaaaaaaaaaaaaaaa" \
			"aaaaaccccaaaaaaccccaaaaa" \
			"aaaaccccccaaaaccccccaaaa" \
			"aaacccaacccaacccaacccaaa" \
			"aaaccaaaaccaaccaaaaccaaa" \
			"aaaccaaaaaaacccaaaacccaa" \
			"aaaccaaaaaaaccaaaaaaccaa" \
			"aaaccacccccaccaaaaaaccaa" \
			"aaaccacccccaccaaaaaaccaa" \
			"aaaccaaaaccaccaaaaaaccaa" \
			"aaaccaaaaccacccaaaacccaa" \
			"aaaccaaaaccaaccaaaaccaaa" \
			"aaacccaacccaacccaacccaaa" \
			"aaaacccccccaaaccccccaaaa" \
			"aaaaacccaccaaaaccccaaaaa" \
			"aaaaaaaaaaaaaaaaaaaaaaaa" \
			"aaaaaaaaaaaaaaaaaaaaaaaa",
	
			"aaaaaaaaaaaaaaaaaaaaaaaa" \
			"aaaaaaaaaaaaaaaaaaaaaaaa" \
			"aaaccccccaaaaaaccccaaaaa" \
			"aaacccccccaaaaccccccaaaa" \
			"aaaccaaacccaacccaacccaaa" \
			"aaaccaaaaccaaccaaaaccaaa" \
			"aaaccaaaaccacccaaaaaaaaa" \
			"aaaccaaacccaccaaaaaaaaaa" \
			"aaacccccccaaccaaaaaaaaaa" \
			"aaaccccccaaaccaaaaaaaaaa" \
			"aaaccaaaaaaaccaaaaaaaaaa" \
			"aaaccaaaaaaacccaaaaaaaaa" \
			"aaaccaaaaaaaaccaaaaccaaa" \
			"aaaccaaaaaaaacccaacccaaa" \
			"aaaccaaaaaaaaaccccccaaaa" \
			"aaaccaaaaaaaaaaccccaaaaa" \
			"aaaaaaaaaaaaaaaaaaaaaaaa" \
			"aaaaaaaaaaaaaaaaaaaaaaaa",
	
			"aaaaaaaaaaaaaaaaaaaaaaaa" \
			"aaaaaaaaaaaaaaaaaaaaaaaa" \
			"aaaaaccccaaaaccccccccaaa" \
			"aaaaccccccaaaccccccccaaa" \
			"aaacccaacccaaaaaccaaaaaa" \
			"aaaccaaaaccaaaaaccaaaaaa" \
			"aaaccaaaaaaaaaaaccaaaaaa" \
			"aaacccaaaaaaaaaaccaaaaaa" \
			"aaaacccccaaaaaaaccaaaaaa" \
			"aaaaacccccaaaaaaccaaaaaa" \
			"aaaaaaaacccaaaaaccaaaaaa" \
			"aaaaaaaaaccaaaaaccaaaaaa" \
			"aaaccaaaaccaaaaaccaaaaaa" \
			"aaacccaacccaaaaaccaaaaaa" \
			"aaaaccccccaaaaaaccaaaaaa" \
			"aaaaaccccaaaaaaaccaaaaaa" \
			"aaaaaaaaaaaaaaaaaaaaaaaa" \
			"aaaaaaaaaaaaaaaaaaaaaaaa",
	
			"aaaaaaaaaaaaaaaaaaaaaaaa" \
			"aaaaaaaaaaaaaaaaaaaaaaaa" \
			"aaaccccccaaaaaaccccaaaaa" \
			"aaacccccccaaaaccccccaaaa" \
			"aaaccaaacccaacccaacccaaa" \
			"aaaccaaaaccaaccaaaaccaaa" \
			"aaaccaaaaccaaccaaaaaaaaa" \
			"aaaccaaacccaacccaaaaaaaa" \
			"aaacccccccaaaacccccaaaaa" \
			"aaaccccccaaaaaacccccaaaa" \
			"aaaccaacccaaaaaaaacccaaa" \
			"aaaccaaacccaaaaaaaaccaaa" \
			"aaaccaaaaccaaccaaaaccaaa" \
			"aaaccaaaacccacccaacccaaa" \
			"aaaccaaaaaccaaccccccaaaa" \
			"aaaccaaaaaccaaaccccaaaaa" \
			"aaaaaaaaaaaaaaaaaaaaaaaa" \
			"aaaaaaaaaaaaaaaaaaaaaaaa",
	
			"........................" \
			"........................" \
			".bbbbbbbbbbbbbbbbbbbbbb." \
			".baaaaaaaaaaaaaaaaaaaab." \
			".baccccccccaaaaaaaaaaab." \
			".baccccccccaaaaaaaaaaab." \
			".baccccccccaaaaaaaaaaab." \
			".baccccccccaaaaaaaaaaab." \
			".baccccccccaaaaaaaaaaab." \
			".baccccccccaaaaaaaaaaab." \
			".baccccccccaaaaaaaaaaab." \
			".baccccccccaaaaaaaaaaab." \
			".baccccccccaaaaaaaaaaab." \
			".baccccccccaaaaaaaaaaab." \
			".baccccccccaaaaaaaaaaab." \
			".baccccccccaaaaaaaaaaab." \
			".bbbbbbbbbbbbbbbbbbbbbb." \
			"........................"};
	
		dst = memory_region(REGION_GFX1);
		memset(dst, 0, 128 * 24 * 24 / 8);
		for (i = 0; i < 128; i++)
		{
			for (y = 0; y < 24; y++)
			{
				for (x = 0; x < 18; x++)
				{
					switch (seg7[y * 18 + x])
					{
					case 'a':
						if ((i & 1) != 0)
							*dst |= 0x80 >> (x & 7);
						break;
					case 'b':
						if ((i & 2) != 0)
							*dst |= 0x80 >> (x & 7);
						break;
					case 'c':
						if ((i & 4) != 0)
							*dst |= 0x80 >> (x & 7);
						break;
					case 'd':
						if ((i & 8) != 0)
							*dst |= 0x80 >> (x & 7);
						break;
					case 'e':
						if ((i & 16) != 0)
							*dst |= 0x80 >> (x & 7);
						break;
					case 'f':
						if ((i & 32) != 0)
							*dst |= 0x80 >> (x & 7);
						break;
					case 'g':
						if ((i & 64) != 0)
							*dst |= 0x80 >> (x & 7);
						break;
					}
					if ((x & 7) == 7)
						dst++;
				}
				dst++;
			}
		}
	
		dst = memory_region(2);
		memset(dst, 0, 24 * 18 * 24 / 8);
		for (i = 0; i < 24; i++)
		{
			for (y = 0; y < 18; y++)
			{
				for (x = 0; x < 24; x++)
				{
					switch (keys[i][y * 24 + x])
					{
					case 'a':
						*dst |= 0x80 >> ((x & 3) * 2);
						break;
					case 'b':
						*dst |= 0x40 >> ((x & 3) * 2);
						break;
					case 'c':
						*dst |= 0xc0 >> ((x & 3) * 2);
						break;
					}
					if ((x & 3) == 3)
						dst++;
				}
			}
		}
	} };
	
	public static InitMachinePtr kim1_init_machine = new InitMachinePtr() { public void handler() 
	{
		UINT8 *RAM = memory_region(REGION_CPU1);
	
		/* setup RAM IRQ vector */
		if (RAM[0x17fa] == 0x00 && RAM[0x17fb] == 0x00)
		{
			RAM[0x17fa] = 0x00;
			RAM[0x17fb] = 0x1c;
		}
		/* setup RAM NMI vector */
		if (RAM[0x17fe] == 0x00 && RAM[0x17ff] == 0x00)
		{
			RAM[0x17fe] = 0x00;
			RAM[0x17ff] = 0x1c;
		}
	
		/* reset the 6530 */
		memset(&m6530, 0, sizeof (m6530));
	
		m6530[0].dria = 0xff;
		m6530[0].drib = 0xff;
		m6530[0].clock = (double) 1000000 / 1;
		m6530[0].timer = timer_pulse(TIME_IN_HZ(256 * m6530[0].clock / 256 / 256), 0, m6530_timer_cb);
	
		m6530[1].dria = 0xff;
		m6530[1].drib = 0xff;
		m6530[1].clock = (double) 1000000 / 1;
		m6530[1].timer = timer_pulse(TIME_IN_HZ(256 * m6530[1].clock / 256 / 256), 1, m6530_timer_cb);
	} };
	
	int kim1_cassette_init(int id)
	{
		const char magic[] = "KIM1";
		char buff[4];
		void *file;
	
		file = image_fopen(IO_CASSETTE, id, OSD_FILETYPE_IMAGE_RW, 0);
		if (file != 0)
		{
			UINT16 addr, size;
			UINT8 ident, *RAM = memory_region(REGION_CPU1);
	
			osd_fread(file, buff, sizeof (buff));
			if (memcmp(buff, magic, sizeof (buff)))
			{
				logerror("kim1_rom_load: magic '%s' not found\n", magic);
				return INIT_FAILED;
			}
			osd_fread_lsbfirst(file, &addr, 2);
			osd_fread_lsbfirst(file, &size, 2);
			osd_fread(file, &ident, 1);
			logerror("kim1_rom_load: $%04X $%04X $%02X\n", addr, size, ident);
			while (size-- > 0)
				osd_fread(file, &RAM[addr++], 1);
			osd_fclose(file);
		}
		return INIT_OK;
	}
	
	void kim1_cassette_exit(int id)
	{
		/* nothing yet */
	}
	
	int kim1_cassette_id(int id)
	{
		const char magic[] = "KIM1";
		char buff[4];
		void *file;
	
		file = image_fopen(IO_CASSETTE, id, OSD_FILETYPE_IMAGE_RW, 0);
		if (file != 0)
		{
			osd_fread(file, buff, sizeof (buff));
			if (memcmp(buff, magic, sizeof (buff)) == 0)
			{
				logerror("kim1_rom_id: magic '%s' found\n", magic);
				return 1;
			}
		}
		return 0;
	}
	
	static void m6530_timer_cb(int chip)
	{
		logerror("m6530(%d) timer expired\n", chip);
		m6530[chip].state |= 0x80;
		if (m6530[chip].irqen)			   /* with IRQ? */
			cpu_set_irq_line(0, 0, HOLD_LINE);
	}
	
	public static InterruptPtr kim1_interrupt = new InterruptPtr() { public int handler() 
	{
		int i;
	
		/* decrease the brightness of the six 7segment LEDs */
		for (i = 0; i < 6; i++)
		{
			if (videoram.read(i * 2 + 1)> 0)
				videoram.read(i * 2 + 1)-= 1;
		}
		return ignore_interrupt();
	} };
	
	INLINE int m6530_r(int chip, int offset)
	{
		int data = 0xff;
	
		switch (offset)
		{
		case 0x00:
		case 0x08:						   /* Data register A */
			if (chip == 1)
			{
				int which = ((m6530[1].drob & m6530[1].ddrb) >> 1) & 0x0f;
	
				switch (which)
				{
				case 0:				   /* key row 1 */
					m6530[1].dria = input_port_0_r(0);
					logerror("read keybd(%d): %c%c%c%c%c%c%c\n",
						 which,
						 (m6530[1].dria & 0x40) ? '.' : '0',
						 (m6530[1].dria & 0x20) ? '.' : '1',
						 (m6530[1].dria & 0x10) ? '.' : '2',
						 (m6530[1].dria & 0x08) ? '.' : '3',
						 (m6530[1].dria & 0x04) ? '.' : '4',
						 (m6530[1].dria & 0x02) ? '.' : '5',
						 (m6530[1].dria & 0x01) ? '.' : '6');
					break;
				case 1:				   /* key row 2 */
					m6530[1].dria = input_port_1_r(0);
					logerror("read keybd(%d): %c%c%c%c%c%c%c\n",
						 which,
						 (m6530[1].dria & 0x40) ? '.' : '7',
						 (m6530[1].dria & 0x20) ? '.' : '8',
						 (m6530[1].dria & 0x10) ? '.' : '9',
						 (m6530[1].dria & 0x08) ? '.' : 'A',
						 (m6530[1].dria & 0x04) ? '.' : 'B',
						 (m6530[1].dria & 0x02) ? '.' : 'C',
						 (m6530[1].dria & 0x01) ? '.' : 'D');
					break;
				case 2:				   /* key row 3 */
					m6530[1].dria = input_port_2_r(0);
					logerror("read keybd(%d): %c%c%c%c%c%c%c\n",
						 which,
						 (m6530[1].dria & 0x40) ? '.' : 'E',
						 (m6530[1].dria & 0x20) ? '.' : 'F',
						 (m6530[1].dria & 0x10) ? '.' : 'a',
						 (m6530[1].dria & 0x08) ? '.' : 'd',
						 (m6530[1].dria & 0x04) ? '.' : '+',
						 (m6530[1].dria & 0x02) ? '.' : 'g',
						 (m6530[1].dria & 0x01) ? '.' : 'p');
					break;
				case 3:				   /* WR4?? */
					m6530[1].dria = 0xff;
					break;
				default:
					m6530[1].dria = 0xff;
					logerror("read DRA(%d) $ff\n", which);
				}
			}
			data = (m6530[chip].dria & ~m6530[chip].ddra) | (m6530[chip].droa & m6530[chip].ddra);
			logerror("m6530(%d) DRA   read : $%02x\n", chip, data);
			break;
		case 0x01:
		case 0x09:						   /* Data direction register A */
			data = m6530[chip].ddra;
			logerror("m6530(%d) DDRA  read : $%02x\n", chip, data);
			break;
		case 0x02:
		case 0x0a:						   /* Data register B */
			data = (m6530[chip].drib & ~m6530[chip].ddrb) | (m6530[chip].drob & m6530[chip].ddrb);
			logerror("m6530(%d) DRB   read : $%02x\n", chip, data);
			break;
		case 0x03:
		case 0x0b:						   /* Data direction register B */
			data = m6530[chip].ddrb;
			logerror("m6530(%d) DDRB  read : $%02x\n", chip, data);
			break;
		case 0x04:
		case 0x0c:						   /* Timer count read (not supported?) */
			data = (int) (256 * timer_timeleft(m6530[chip].timer) / TIME_IN_HZ(m6530[chip].clock));
			m6530[chip].irqen = (offset & 8) ? 1 : 0;
			logerror("m6530(%d) TIMR  read : $%02x%s\n", chip, data, (offset & 8) ? " (IRQ)" : "");
			break;
		case 0x05:
		case 0x0d:						   /* Timer count read (not supported?) */
			data = (int) (256 * timer_timeleft(m6530[chip].timer) / TIME_IN_HZ(m6530[chip].clock));
			m6530[chip].irqen = (offset & 8) ? 1 : 0;
			logerror("m6530(%d) TIMR  read : $%02x%s\n", chip, data, (offset & 8) ? " (IRQ)" : "");
			break;
		case 0x06:
		case 0x0e:						   /* Timer count read */
			data = (int) (256 * timer_timeleft(m6530[chip].timer) / TIME_IN_HZ(m6530[chip].clock));
			m6530[chip].irqen = (offset & 8) ? 1 : 0;
			logerror("m6530(%d) TIMR  read : $%02x%s\n", chip, data, (offset & 8) ? " (IRQ)" : "");
			break;
		case 0x07:
		case 0x0f:						   /* Timer status read */
			data = m6530[chip].state;
			m6530[chip].state &= ~0x80;
			m6530[chip].irqen = (offset & 8) ? 1 : 0;
			logerror("m6530(%d) STAT  read : $%02x%s\n", chip, data, (offset & 8) ? " (IRQ)" : "");
			break;
		}
		return data;
	}
	
	READ_HANDLER ( m6530_003_r )
	{
		return m6530_r(0, offset);
	}
	READ_HANDLER ( m6530_002_r )
	{
		return m6530_r(1, offset);
	}
	
	READ_HANDLER ( kim1_mirror_r )
	{
		return cpu_readmem16(offset & 0x1fff);
	}
	
	static void m6530_w(int chip, int offset, int data)
	{
		switch (offset)
		{
		case 0x00:
		case 0x08:						   /* Data register A */
			logerror("m6530(%d) DRA  write: $%02x\n", chip, data);
			m6530[chip].droa = data;
			if (chip == 1)
			{
				int which = (m6530[1].drob & m6530[1].ddrb) >> 1;
	
				switch (which)
				{
				case 0:				   /* key row 1 */
					break;
				case 1:				   /* key row 2 */
					break;
				case 2:				   /* key row 3 */
					break;
				case 3:				   /* WR4?? */
					break;
					/* write LED # 1-6 */
				case 4:
				case 5:
				case 6:
				case 7:
				case 8:
				case 9:
					if ((data & 0x80) != 0)
					{
						logerror("write 7seg(%d): %c%c%c%c%c%c%c\n",
							 which + 1 - 4,
							 (data & 0x01) ? 'a' : '.',
							 (data & 0x02) ? 'b' : '.',
							 (data & 0x04) ? 'c' : '.',
							 (data & 0x08) ? 'd' : '.',
							 (data & 0x10) ? 'e' : '.',
							 (data & 0x20) ? 'f' : '.',
							 (data & 0x40) ? 'g' : '.');
						videoram.write((which - 4) * 2 + 0,data & 0x7f);
						videoram.write((which - 4) * 2 + 1,15);
					}
				}
			}
			break;
		case 0x01:
		case 0x09:						   /* Data direction register A */
			logerror("m6530(%d) DDRA  write: $%02x\n", chip, data);
			m6530[chip].ddra = data;
			break;
		case 0x02:
		case 0x0a:						   /* Data register B */
			logerror("m6530(%d) DRB   write: $%02x\n", chip, data);
			m6530[chip].drob = data;
			if (chip == 1)
			{
				int which = m6530[1].ddrb & m6530[1].drob;
	
				if ((which & 0x3f) == 0x27)
				{
					/* This is the cassette output port */
					logerror("write cassette port: %d\n", (which & 0x80) ? 1 : 0);
					DAC_signed_data_w(0, (which & 0x80) ? 255 : 0);
				}
			}
			break;
		case 0x03:
		case 0x0b:						   /* Data direction register B */
			logerror("m6530(%d) DDRB  write: $%02x\n", chip, data);
			m6530[chip].ddrb = data;
			break;
		case 0x04:
		case 0x0c:						   /* Timer 1 start */
			logerror("m6530(%d) TMR1  write: $%02x%s\n", chip, data, (offset & 8) ? " (IRQ)" : "");
			m6530[chip].state &= ~0x80;
			m6530[chip].irqen = (offset & 8) ? 1 : 0;
			if (m6530[chip].timer)
				timer_remove(m6530[chip].timer);
			m6530[chip].clock = (double) 1000000 / 1;
			m6530[chip].timer = timer_pulse(TIME_IN_HZ((data + 1) * m6530[chip].clock / 256 / 256), chip, m6530_timer_cb);
			break;
		case 0x05:
		case 0x0d:						   /* Timer 8 start */
			logerror("m6530(%d) TMR8  write: $%02x%s\n", chip, data, (offset & 8) ? " (IRQ)" : "");
			m6530[chip].state &= ~0x80;
			m6530[chip].irqen = (offset & 8) ? 1 : 0;
			if (m6530[chip].timer)
				timer_remove(m6530[chip].timer);
			m6530[chip].clock = (double) 1000000 / 8;
			m6530[chip].timer = timer_pulse(TIME_IN_HZ((data + 1) * m6530[chip].clock / 256 / 256), chip, m6530_timer_cb);
			break;
		case 0x06:
		case 0x0e:						   /* Timer 64 start */
			logerror("m6530(%d) TMR64 write: $%02x%s\n", chip, data, (offset & 8) ? " (IRQ)" : "");
			m6530[chip].state &= ~0x80;
			m6530[chip].irqen = (offset & 8) ? 1 : 0;
			if (m6530[chip].timer)
				timer_remove(m6530[chip].timer);
			m6530[chip].clock = (double) 1000000 / 64;
			m6530[chip].timer = timer_pulse(TIME_IN_HZ((data + 1) * m6530[chip].clock / 256 / 256), chip, m6530_timer_cb);
			break;
		case 0x07:
		case 0x0f:						   /* Timer 1024 start */
			logerror("m6530(%d) TMR1K write: $%02x%s\n", chip, data, (offset & 8) ? " (IRQ)" : "");
			m6530[chip].state &= ~0x80;
			m6530[chip].irqen = (offset & 8) ? 1 : 0;
			if (m6530[chip].timer)
				timer_remove(m6530[chip].timer);
			m6530[chip].clock = (double) 1000000 / 1024;
			m6530[chip].timer = timer_pulse(TIME_IN_HZ((data + 1) * m6530[chip].clock / 256 / 256), chip, m6530_timer_cb);
			break;
		}
	}
	
	WRITE_HANDLER ( m6530_003_w )
	{
		m6530_w(0, offset, data);
	}
	WRITE_HANDLER ( m6530_002_w )
	{
		m6530_w(1, offset, data);
	}
	
	WRITE_HANDLER ( kim1_mirror_w )
	{
		cpu_writemem16(offset & 0x1fff, data);
	}
}
