/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */ 
package includes;

public class avigoH
{
	#define AVIGO_NUM_COLOURS 2
	
	#define AVIGO_SCREEN_WIDTH        160
	#define AVIGO_SCREEN_HEIGHT       240
	
	READ_HANDLER(avigo_vid_memory_r);
	WRITE_HANDLER(avigo_vid_memory_w);
	
	
	void avigo_init_palette(UBytePtr sys_palette, unsigned short *sys_colortable, const UBytePtr color_prom);
}
