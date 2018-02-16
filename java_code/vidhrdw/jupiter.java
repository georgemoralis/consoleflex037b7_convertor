/***************************************************************************

  jupiter.c

  Functions to emulate the video hardware of the Jupiter Ace.

***************************************************************************/

/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */ 
package vidhrdw;

public class jupiter
{
	
	UBytePtr jupiter_charram;
	size_t jupiter_charram_size;
	
	public static VhStartPtr jupiter_vh_start = new VhStartPtr() { public int handler() 
	{
		if( generic_vh_start() )
			return 1;
	    return 0;
	} };
	
	public static VhStopPtr jupiter_vh_stop = new VhStopPtr() { public void handler() 
	{
		generic_vh_stop();
	} };
	
	public static WriteHandlerPtr jupiter_vh_charram_w = new WriteHandlerPtr() {public void handler(int offset, int data)
	{
		int chr = offset / 8, offs;
	
	    if( data == jupiter_charram[offset] )
			return; /* no change */
	
	    jupiter_charram[offset] = data;
	
	    /* decode character graphics again */
		decodechar(Machine.gfx[0], chr, jupiter_charram, &jupiter_charlayout);
	
	    /* mark all visible characters with that code dirty */
	    for( offs = 0; offs < videoram_size[0]; offs++ )
		{
			if( videoram[offs] == chr )
				dirtybuffer[offs] = 1;
		}
	} };
	
	public static VhUpdatePtr jupiter_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap,int full_refresh) 
	{
	
		int offs;
	
		/* do we need a full refresh? */
	    if (full_refresh != 0)
			memset(dirtybuffer, 1, videoram_size[0]);
	
	    for( offs = 0; offs < videoram_size[0]; offs++ )
		{
	        if( dirtybuffer[offs]  )
			{
	            int code = videoram.read(offs);
				int sx, sy;
	
				sy = (offs / 32) * 8;
				sx = (offs % 32) * 8;
	
				drawgfx(bitmap, Machine.gfx[0], code & 0x7f, (code & 0x80) ? 1 : 0, 0,0, sx,sy,
					&Machine.visible_area, TRANSPARENCY_NONE, 0);
	
	            dirtybuffer[offs] = 0;
			}
		}
	} };
}
