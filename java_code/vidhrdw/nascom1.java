/***************************************************************************

  nascom1.c

  Functions to emulate the video hardware of the nascom1.

***************************************************************************/

/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */ 
package vidhrdw;

public class nascom1
{
	
	public static VhStartPtr nascom1_vh_start = new VhStartPtr() { public int handler() 
	{
	
		if( generic_vh_start() )
			return 1;
	    return 0;
	} };
	
	public static VhStopPtr nascom1_vh_stop = new VhStopPtr() { public void handler() 
	{
		generic_vh_stop();
	} };
	
	public static VhUpdatePtr nascom1_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap,int full_refresh) 
	{
	
	int	sy, sx;
	
	if (full_refresh != 0) memset (dirtybuffer, 1, videoram_size[0]);
	
	for (sx = 0; sx < 48; sx++) {
	  if (dirtybuffer[sx + 0x03ca]) {
		drawgfx (bitmap, Machine.gfx[0], videoram.read(0x03ca + sx),
					  1, 0, 0, sx * 8, 0, &Machine.visible_area,
					  TRANSPARENCY_NONE, 0);
	    dirtybuffer[0x03ca + sx] = 0;
	  }
	}
	
	for (sy = 0; sy < 15; sy++) {
	  for (sx = 0; sx < 48; sx++) {
	    if (dirtybuffer[0x000a + (sy * 64) + sx]) {
		  drawgfx (bitmap, Machine.gfx[0], videoram.read(0x000a + (sy * 64) + sx),
					  1, 0, 0, sx * 8, (sy + 1) * 16, &Machine.visible_area,
					  TRANSPARENCY_NONE, 0);
		  dirtybuffer[0x000a + (sy * 64) + sx] = 0;
		}
	  }
	}
	
	
	} };
	
	public static VhUpdatePtr nascom2_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap,int full_refresh) 
	{
	
	int	sy, sx;
	
	if (full_refresh != 0) memset (dirtybuffer, 1, videoram_size[0]);
	
	for (sx = 0; sx < 48; sx++) {
	  if (dirtybuffer[sx + 0x03ca]) {
		drawgfx (bitmap, Machine.gfx[0], videoram.read(0x03ca + sx),
					  1, 0, 0, sx * 8, 0, &Machine.visible_area,
					  TRANSPARENCY_NONE, 0);
	    dirtybuffer[0x03ca + sx] = 0;
	  }
	}
	
	for (sy = 0; sy < 15; sy++) {
	  for (sx = 0; sx < 48; sx++) {
	    if (dirtybuffer[0x000a + (sy * 64) + sx]) {
		  drawgfx (bitmap, Machine.gfx[0], videoram.read(0x000a + (sy * 64) + sx),
					  1, 0, 0, sx * 8, (sy + 1) * 14, &Machine.visible_area,
					  TRANSPARENCY_NONE, 0);
		  dirtybuffer[0x000a + (sy * 64) + sx] = 0;
		}
	  }
	}
	
	
	} };
	
}
