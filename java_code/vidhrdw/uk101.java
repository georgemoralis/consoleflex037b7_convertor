/***************************************************************************

  uk101.c

  Functions to emulate the video hardware of the UK101.

***************************************************************************/

/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */ 
package vidhrdw;

public class uk101
{
	
	public static VhStartPtr uk101_vh_start = new VhStartPtr() { public int handler() 
	{
		if (generic_vh_start ())
			return (1);
	
		return (0);
	} };
	
	public static VhStopPtr uk101_vh_stop = new VhStopPtr() { public void handler() 
	
	{
	
	generic_vh_stop ();
	
	} };
	
	/* || */
	
	public static VhUpdatePtr uk101_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap,int full_refresh) 
	{
		int sx, sy;
	
		if (full_refresh != 0)
			memset (dirtybuffer, 1, videoram_size[0]);
	
		for (sy = 0; sy < 25; sy++) {
			for (sx = 0; sx < 32; sx++) {
				if (dirtybuffer[0x84 + sy * 32 + sx]) {
					drawgfx (bitmap, Machine.gfx[0], videoram.read(0x84 + sy * 32 + sx), 1,
					0, 0, sx * 8, sy * 16, &Machine.visible_area,
					TRANSPARENCY_NONE, 0);
					dirtybuffer[0x84 + sy * 32 + sx] = 0;
				}
			}
		}
	} };
	
	public static VhUpdatePtr superbrd_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap,int full_refresh) 
	{
		int sx, sy;
	
		if (full_refresh != 0)
			memset (dirtybuffer, 1, videoram_size[0]);
	
		for (sy = 0; sy < 16; sy++) {
			for (sx = 0; sx < 64; sx++) {
				if (dirtybuffer[sy * 64 + sx]) {
					drawgfx (bitmap, Machine.gfx[0], videoram.read(sy * 64 + sx), 1,
					0, 0, sx * 8, sy * 16, &Machine.visible_area,
					TRANSPARENCY_NONE, 0);
					dirtybuffer[sy * 64 + sx] = 0;
				}
			}
		}
	} };
}
