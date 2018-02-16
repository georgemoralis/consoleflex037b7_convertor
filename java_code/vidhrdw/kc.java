/***************************************************************************

  kc.c

  Functions to emulate the video hardware of the kc85/4.

***************************************************************************/

/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */ 
package vidhrdw;

public class kc
{
	
	/***************************************************************************
	  Start the video hardware emulation.
	***************************************************************************/
	
	extern UBytePtr kc85_4_display_video_ram;
	
	public static VhStartPtr kc85_4_vh_start = new VhStartPtr() { public int handler() 
	{
	
		return 0;
	} };
	
	public static VhStopPtr kc85_4_vh_stop = new VhStopPtr() { public void handler()  
	{
	
	} };
	
	
	/***************************************************************************
	  Draw the game screen in the given osd_bitmap.
	  Do NOT call osd_update_display() from this function,
	  it will be called by the main emulation engine.
	***************************************************************************/
	public static VhUpdatePtr kc85_4_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap,int full_refresh) 
	{
	        UBytePtr pixel_ram = kc85_4_display_video_ram;
	        UBytePtr colour_ram = pixel_ram + 0x04000;
	
	        int i;
	
	        for (i=0; i<0x04000; i++)
	        {
	                int a;
	                int gfx_byte;
	                int x,y;
	                int foreground_pen, background_pen;
	                int colour;
	
	                x = ((i>>8) & 0x03f)*8;
	                y = (i & 0x0ff);
	
	                //x = (i>>5) & 0x01f8;
	                //y = i & 0x0ff;
	
	                if (x>=320)
	                        x = 319;
	
	                if (y>=(32*8))
	                        y = (32*8)-1;
	
	
	                colour = colour_ram[i];
	
	                background_pen = (colour&7) | 0x010;
	                foreground_pen = (colour>>3) & 0x015;
	
	                background_pen = Machine.pens[background_pen];
	                foreground_pen = Machine.pens[foreground_pen];
	
	                gfx_byte = pixel_ram[i];
	
	                for (a=0; a<8; a++)
	                {
	                        int pen;
	
	                        if ((gfx_byte & 0x080) != 0)
	                        {
	                            pen = foreground_pen;
	                        }
	                        else
	                        {
	                            pen = background_pen;
	                        }
	
	                        plot_pixel(bitmap, x+a, y, pen);
	
	                        gfx_byte = gfx_byte<<1;
	                }
	
	
	        }
	
	} };
	
}
