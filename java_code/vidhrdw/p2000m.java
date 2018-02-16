/**********************************************************************

	p2000m.c

	Functions to emulate video hardware of the p2000m

**********************************************************************/

/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */ 
package vidhrdw;

public class p2000m
{
	
	static	INT8	frame_count;
	
	public static VhStartPtr p2000m_vh_start = new VhStartPtr() { public int handler() 
	{
		frame_count = 0;
		if (generic_vh_start ())
			return (1);
		return (0);
	} };
	
	public static VhStopPtr p2000m_vh_stop = new VhStopPtr() { public void handler() 
	{
		generic_vh_stop ();
	} };
	
	void p2000m_vh_callback (void)
	{
	
		int	offs;
	
		if (frame_count++ > 49) frame_count = 0;
		if ((frame_count == 24) | !frame_count)
		{
			for (offs = 0; offs < 2047; offs++)
			{
				if (videoram.read(offs + 2048)& 0x40)
					dirtybuffer[offs] = 1;
			}
		}
	}
	
	public static VhUpdatePtr p2000m_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap,int full_refresh) 
	{
		int offs, sx, sy, code, loop;
	
		if (full_refresh != 0)
			memset (dirtybuffer, 1, videoram_size[0]);
	
		for (offs = 0; offs < 80 * 24; offs++)
		{
			if (dirtybuffer[offs] | dirtybuffer[offs + 2048])
			{
				sy = (offs / 80) * 10;
				sx = (offs % 80) * 6;
				if ((frame_count > 25) && (videoram.read(offs + 2048)& 0x40)) code = 32;
				else
				{
					code = videoram.read(offs);
					if ((videoram.read(offs + 2048)& 0x01) && (code & 0x20))
					{
						code += (code & 0x40) ? 64 : 96;
					} else {
						code &= 0x7f;
					}
					if (code < 32) code = 32;
				}
	
				drawgfx (bitmap, Machine.gfx[0], code,
					videoram.read(offs + 2048)& 0x08 ? 0 : 1, 0, 0, sx, sy,
					&Machine.visible_area, TRANSPARENCY_NONE, 0);
				if (videoram.read(offs)& 0x80)
				{
					for (loop = 0; loop < 6; loop++)
					{
						plot_pixel (bitmap, sx + loop, sy + 9, 1);
					}
				}
				dirtybuffer[offs] = 0;
			}
		}
	} };
}
