/******************************************************************************
 *	Microtan 65
 *
 *	variables and function prototypes
 *
 *	Juergen Buchmueller <pullmoll@t-online.de>, Jul 2000
 *
 *	Thanks go to Geoff Macdonald <mail@geoff.org.uk>
 *	for his site http:://www.geo255.redhotant.com
 *	and to Fabrice Frances <frances@ensica.fr>
 *	for his site http://www.ifrance.com/oric/microtan.html
 *
 ******************************************************************************/

/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */ 
package includes;

public class microtanH
{
	
	/* from src/mess/vidhrdw/microtan.c */
	
	extern UINT8 microtan_chunky_graphics;
	extern UINT8 *microtan_chunky_buffer;
	
	/* from src/mess/machine/microtan.c */
	extern extern 
	extern int microtan_cassette_id(int id);
	extern int microtan_cassette_init(int id);
	extern void microtan_cassette_exit(int id);
	
	extern int microtan_snapshot_id(int id);
	extern int microtan_snapshot_init(int id);
	extern void microtan_snapshot_exit(int id);
	
	extern int microtan_hexfile_id(int id);
	extern int microtan_hexfile_init(int id);
	extern void microtan_hexfile_exit(int id);
	
	extern 
	extern READ_HANDLER ( microtan_via_0_r );
	extern READ_HANDLER ( microtan_via_1_r );
	extern READ_HANDLER ( microtan_bffx_r );
	extern READ_HANDLER ( microtan_sound_r );
	extern READ_HANDLER ( microtan_sio_r );
	
	extern WRITE_HANDLER ( microtan_via_0_w );
	extern WRITE_HANDLER ( microtan_via_1_w );
	extern WRITE_HANDLER ( microtan_bffx_w );
	extern WRITE_HANDLER ( microtan_sound_w );
	extern WRITE_HANDLER ( microtan_sio_w );
	
	/* from src/mess/vidhrdw/microtan.c */
	extern char microtan_frame_message[64+1];
	extern int microtan_frame_time;
	
	extern WRITE_HANDLER ( microtan_videoram_w );
	
	extern extern extern extern }
