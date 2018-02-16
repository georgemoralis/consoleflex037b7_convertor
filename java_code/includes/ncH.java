/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */ 
package includes;

public class ncH
{
	#define NC_NUM_COLOURS 2
	
	#define NC_SCREEN_WIDTH        480
	#define NC_SCREEN_HEIGHT       64
	
	#define NC200_SCREEN_WIDTH		480
	#define NC200_SCREEN_HEIGHT		128
	
	void nc_init_palette(UBytePtr sys_palette, unsigned short *sys_colortable, const UBytePtr color_prom);
	
	void    nc_set_card_present_state(int);
	void    nc_set_card_write_protect_state(int);
	int     nc_pcmcia_card_id(int);
	int    nc_pcmcia_card_load(int);
	void    nc_pcmcia_card_exit(int);
	
}
