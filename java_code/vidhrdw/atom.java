/******************************************************************************

atom.c

******************************************************************************/

/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */ 
package vidhrdw;

public class atom
{
	
	public static VhStartPtr atom_vh_start = new VhStartPtr() { public int handler() 
	{
		if (m6847_vh_start())
			return (1);
	
		m6847_set_vram(memory_region(REGION_CPU1) + 0x8000, 0xffff);
		return (0);
	} };
	
}
