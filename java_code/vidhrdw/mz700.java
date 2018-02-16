/***************************************************************************
 *	Sharp MZ700
 *
 *	video hardware
 *
 *	Juergen Buchmueller <pullmoll@t-online.de>, Jul 2000
 *
 *	Reference: http://sharpmz.computingmuseum.com
 *
 ***************************************************************************/

/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */ 
package vidhrdw;

public class mz700
{
	
	#ifndef VERBOSE
	#define VERBOSE 1
	#endif
	
	#if VERBOSE
	#define LOG(N,M,A)	\
		if(VERBOSE>=N){ if (M != 0)logerror("%11.6f: %-24s",timer_get_time(),(char*)M ); logerror A; }
	#else
	#define LOG(N,M,A)
	#endif
	
	char mz700_frame_message[64+1];
	int mz700_frame_time = 0;
	
	public static VhConvertColorPromPtr mz700_init_colors = new VhConvertColorPromPtr() { public void handler(char []palette, char []colortable, UBytePtr color_prom) 
	{
		int i;
	
	    for (i = 0; i < 8; i++)
		{
			palette[i*3+0] = (i & 2) ? 0xff : 0x00;
			palette[i*3+1] = (i & 4) ? 0xff : 0x00;
			palette[i*3+2] = (i & 1) ? 0xff : 0x00;
		}
	
		for (i = 0; i < 256; i++)
		{
			colortable[i*2+0] = i & 7;
	        colortable[i*2+1] = (i >> 4) & 7;
		}
	} };
	
	public static VhStartPtr mz700_vh_start = new VhStartPtr() { public int handler() 
	{
		if (generic_vh_start())
			return 1;
	    return 0;
	} };
	
	public static VhStopPtr mz700_vh_stop = new VhStopPtr() { public void handler() 
	{
		generic_vh_stop();
	} };
	
	public static VhUpdatePtr mz700_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap,int full_refresh) 
	{
	    int offs;
	
		if( mz700_frame_time > 0 )
	    {
			ui_text(bitmap, mz700_frame_message, 1, Machine.visible_area.max_y - 9);
	        /* if the message timed out, clear it on the next frame */
			if( --mz700_frame_time == 0 )
				full_refresh = 1;
	    }
	
	    if (full_refresh != 0)
		{
			fillbitmap(Machine.scrbitmap, Machine.pens[0], &Machine.visible_area);
			memset(dirtybuffer, 1, videoram_size[0]);
	    }
	
		for( offs = 0; offs < 40*25; offs++ )
		{
			if( dirtybuffer[offs] )
			{
				int sx, sy, code, color;
	
	            dirtybuffer[offs] = 0;
	
	            sy = (offs / 40) * 8;
				sx = (offs % 40) * 8;
				code = videoram.read(offs);
				color = colorram.read(offs);
				code |= (color & 0x80) << 1;
	
	            drawgfx(bitmap,Machine.gfx[0],code,color,0,0,sx,sy,
					&Machine.visible_area,TRANSPARENCY_NONE,0);
			}
		}
	} };
	
	
}
