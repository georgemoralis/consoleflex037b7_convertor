/******************************************************************************
 *	Sharp MZ700
 *
 *	variables and function prototypes
 *
 *	Juergen Buchmueller <pullmoll@t-online.de>, Jul 2000
 *
 *  Reference: http://sharpmz.computingmuseum.com
 *
 ******************************************************************************/

/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */ 
package includes;

public class mz700H
{
	
	/* from src/mess/machine/mz700.c */
	extern extern extern 
	extern int mz700_cassette_id(int id);
	extern int mz700_cassette_init(int id);
	extern void mz700_cassette_exit(int id);
	
	extern 
	extern READ_HANDLER ( mz700_mmio_r );
	extern WRITE_HANDLER ( mz700_mmio_w );
	extern WRITE_HANDLER ( mz700_bank_w );
	
	/* from src/mess/vidhrdw/mz700.c */
	
	extern char mz700_frame_message[64+1];
	extern int mz700_frame_time;
	
	extern extern extern extern 
	/* from src/mess/sndhrdw/mz700.c */
	extern extern extern extern void mz700_sh_set_clock(int clock);
	
	/******************************************************************************
	 *	Sharp MZ800
	 *
	 ******************************************************************************/
	extern extern extern extern 
	extern extern extern extern extern extern WRITE_HANDLER ( mz800_bank_w );
	extern extern extern 
	extern extern extern extern extern 
	extern }
