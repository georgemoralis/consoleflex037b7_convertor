/******************************************************************************
 *
 *	kaypro.h
 *
 *	interface for Kaypro 2x
 *
 *	Juergen Buchmueller, July 1998
 *
 ******************************************************************************/

/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */ 
package includes;

public class kayproH
{
	
	extern int kaypro_floppy_init(int id);
	extern extern extern 
	extern 
	#define KAYPRO_FONT_W 	8
	#define KAYPRO_FONT_H 	16
	#define KAYPRO_SCREEN_W	80
	#define KAYPRO_SCREEN_H   25
	
	extern extern extern void kaypro_vh_screenrefresh(struct osd_bitmap * bitmap, int full_refresh);
	
	extern READ_HANDLER ( kaypro_const_r );
	extern WRITE_HANDLER ( kaypro_const_w );
	extern READ_HANDLER ( kaypro_conin_r );
	extern WRITE_HANDLER ( kaypro_conin_w );
	extern READ_HANDLER ( kaypro_conout_r );
	extern WRITE_HANDLER ( kaypro_conout_w );
	
	extern extern extern 
	extern extern }
