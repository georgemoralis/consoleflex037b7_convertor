/******************************************************************************
	Atari 400/800

	Video handler

	Juergen Buchmueller, June 1998
******************************************************************************/

/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */ 
package vidhrdw;

public class atari
{
	
	#ifdef	LSB_FIRST
	#define BYTE_XOR(n) (n)
	#else
	#define BYTE_XOR(n) ((n)^1)
	#endif
	
	#define VERBOSE 0
	
	#if VERBOSE
	#define LOG(x)	if (errorlog != 0) fprintf x
	#else
	#define LOG(x)	/* x */
	#endif
	
	char atari_frame_message[64+1];
	int atari_frame_counter;
	
	/* flag for displaying television artifacts in ANTIC mode F (15) */
	static int tv_artifacts = 0;
	
	/*************************************************************************
	 * The priority tables tell which playfield, player or missile colors
	 * have precedence about the others, depending on the contents of the
	 * "prior" register. There are 64 possible priority selections.
	 * The table is here to make it easier to build the 'illegal' priority
	 * combinations that produce black or 'ILL' color.
	 *************************************************************************/
	
	/*************************************************************************
	 * calculate player/missile priorities (GTIA prior at $D00D)
	 * prior   color priorities in descending order
	 * ------------------------------------------------------------------
	 * bit 0   PL0	  PL1	 PL2	PL3    PF0	  PF1	 PF2	PF3/P4 BK
	 *		   all players in front of all playfield colors
	 * bit 1   PL0	  PL1	 PF0	PF1    PF2	  PF3/P4 PL2	PL3    BK
	 *		   pl 0+1 in front of pf 0-3 in front of pl 2+3
	 * bit 2   PF0    PF1    PF2    PF3/P4 PL0    PL1    PL2    PL3    BK
	 *		   all playfield colors in front of all players
	 * bit 3   PF0    PF1    PL0    PL1    PL2    PL3    PF2    PF3/P4 BK
	 *		   pf 0+1 in front of all players in front of pf 2+3
	 * bit 4   missiles colors are PF3 (P4)
	 *		   missiles have the same priority as pf3
	 * bit 5   PL0+PL1 and PL2+PL3 bits xored
	 *		   00: playfield, 01: PL0/2, 10: PL1/3 11: black (EOR)
	 * bit 7+6 CTIA mod (00) or GTIA mode 1 to 3 (01, 10, 11)
	 *************************************************************************/
	
	/* player/missile #4 color is equal to playfield #3 */
	#define PM4 PF3
	
	/* bit masks for players and missiles */
	#define P0 0x01
	#define P1 0x02
	#define P2 0x04
	#define P3 0x08
	#define M0 0x10
	#define M1 0x20
	#define M2 0x40
	#define M3 0x80
	
	/************************************************************************
	 * Contents of the following table:
	 *
	 * PL0 -PL3  are the player/missile colors 0 to 3
	 * P000-P011 are the 4 available color clocks for playfield color 0
	 * P100-P111 are the 4 available color clocks for playfield color 1
	 * P200-P211 are the 4 available color clocks for playfield color 2
	 * P300-P311 are the 4 available color clocks for playfield color 3
	 * ILL       is some undefined color. On my 800XL it looked light yellow ;)
	 *
	 * Each line holds the 8 bitmasks and resulting colors for player and
	 * missile number 0 to 3 in their fixed priority order.
	 * The 8 lines per block are for the 8 available playfield colors.
	 * Yes, 8 colors because the text modes 2,3 and graphics mode F can
	 * be combined with players. The result is the players color with
	 * luminance of the modes foreground (ie. colpf1).
	 * Any combination of players/missiles (256) is checked for the highest
	 * priority player or missile and the resulting color is stored into
	 * antic.prio_table. The second part (20-3F) contains the resulting
	 * color values for the EOR mode, which is derived from the *visible*
	 * player/missile colors calculated for the first part (00-1F).
	 * The priorities of combining priority bits (which games use!) are:
	 ************************************************************************/
	static	UINT8	_pm_colors[32][8*2*8] = {
		{
			M0, PL0,P0, PL0,M1, PL1,P1, PL1,M2, PL2,P2, PL2,M3, PL3,P3, PL3,  // 00
			M0, PL0,P0, PL0,M1, PL1,P1, PL1,M2, PL2,P2, PL2,M3, PL3,P3, PL3,
			M0, PL0,P0, PL0,M1, PL1,P1, PL1,M2, PL2,P2, PL2,M3, PL3,P3, PL3,
			 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0,
			M0,P000,P0,P000,M1,P100,P1,P100,M2,P200,P2,P200,M3,P300,P3,P300,
			M0,P001,P0,P001,M1,P101,P1,P101,M2,P201,P2,P201,M3,P301,P3,P301,
			M0,P010,P0,P010,M1,P110,P1,P110,M2,P210,P2,P210,M3,P310,P3,P310,
			M0,P011,P0,P011,M1,P111,P1,P111,M2,P211,P2,P211,M3,P311,P3,P311
		},
		{
			M0, PL0,P0, PL0,M1, PL1,P1, PL1,M2, PL2,P2, PL2,M3, PL3,P3, PL3,  // 01
			M0, PL0,P0, PL0,M1, PL1,P1, PL1,M2, PL2,P2, PL2,M3, PL3,P3, PL3,
			M0, PL0,P0, PL0,M1, PL1,P1, PL1,M2, PL2,P2, PL2,M3, PL3,P3, PL3,
			 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0,
			M0,P000,P0,P000,M1,P100,P1,P100,M2,P200,P2,P200,M3,P300,P3,P300,
			M0,P001,P0,P001,M1,P101,P1,P101,M2,P201,P2,P201,M3,P301,P3,P301,
			M0,P010,P0,P010,M1,P110,P1,P110,M2,P210,P2,P210,M3,P310,P3,P310,
			M0,P011,P0,P011,M1,P111,P1,P111,M2,P211,P2,P211,M3,P311,P3,P311
		},
		{
			M0, PL0,P0, PL0,M1, PL1,P1, PL1,M2, PL2,P2, PL2,M3, PL3,P3, PL3,  // 02
			M0, PL0,P0, PL0,M1, PL1,P1, PL1,M2,   0,P2,   0,M3,   0,P3,   0,
			M0, PL0,P0, PL0,M1, PL1,P1, PL1,M2,   0,P2,   0,M3,   0,P3,   0,
			 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0,
			M0,P000,P0,P000,M1,P100,P1,P100,M2,P200,P2,P200,M3,P300,P3,P300,
			M0,P001,P0,P001,M1,P101,P1,P101,M2,P201,P2,P201,M3,P301,P3,P301,
			M0,P010,P0,P010,M1,P110,P1,P110,M2,P210,P2,P210,M3,P310,P3,P310,
			M0,P011,P0,P011,M1,P111,P1,P111,M2,P211,P2,P211,M3,P311,P3,P311
		},
		{
			M0, PL0,P0, PL0,M1, PL1,P1, PL1,M2, PL2,P2, PL2,M3, PL3,P3, PL3,  // 03
			M0, PL0,P0, PL0,M1, PL1,P1, PL1,M2, PL2,P2, PL2,M3, PL3,P3, PL3,
			M0, PL0,P0, PL0,M1, PL1,P1, PL1,M2, ILL,P2, ILL,M3, ILL,P3, ILL,
			 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0,
			M0,P000,P0,P000,M1,P100,P1,P100,M2,P200,P2,P200,M3,P300,P3,P300,
			M0,P001,P0,P001,M1,P101,P1,P101,M2,P201,P2,P201,M3,P301,P3,P301,
			M0,P010,P0,P010,M1,P110,P1,P110,M2,P210,P2,P210,M3,P310,P3,P310,
			M0,P011,P0,P011,M1,P111,P1,P111,M2,P211,P2,P211,M3,P311,P3,P311
		},
		{
			M0, PL0,P0, PL0,M1, PL1,P1, PL1,M2, PL2,P2, PL2,M3, PL3,P3, PL3,  // 04
			M0,   0,P0,   0,M1,   0,P1,   0,M2,   0,P2,   0,M3,   0,P3,   0,
			M0,   0,P0,   0,M1,   0,P1,   0,M2,   0,P2,   0,M3,   0,P3,   0,
			 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0,
			M0,P000,P0,P000,M1,P100,P1,P100,M2,P200,P2,P200,M3,P300,P3,P300,
			M0,P001,P0,P001,M1,P101,P1,P101,M2,P201,P2,P201,M3,P301,P3,P301,
			M0,P010,P0,P010,M1,P110,P1,P110,M2,P210,P2,P210,M3,P310,P3,P310,
			M0,P011,P0,P011,M1,P111,P1,P111,M2,P211,P2,P211,M3,P311,P3,P311
		},
		{
			M0, PL0,P0, PL0,M1, PL1,P1, PL1,M2, PL2,P2, PL2,M3, PL3,P3, PL3,  // 05
			M0, ILL,P0, ILL,M1, ILL,P1, ILL,M2, PL2,P2, PL2,M3, PL3,P3, PL3,
			M0,   0,P0,   0,M1,   0,P1,   0,M2, ILL,P2, ILL,M3, ILL,P3, ILL,
			 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0,
			M0,P000,P0,P000,M1,P100,P1,P100,M2,P200,P2,P200,M3,P300,P3,P300,
			M0,P001,P0,P001,M1,P101,P1,P101,M2,P201,P2,P201,M3,P301,P3,P301,
			M0,P010,P0,P010,M1,P110,P1,P110,M2,P210,P2,P210,M3,P310,P3,P310,
			M0,P011,P0,P011,M1,P111,P1,P111,M2,P211,P2,P211,M3,P311,P3,P311
		},
		{
			M0, PL0,P0, PL0,M1, PL1,P1, PL1,M2, PL2,P2, PL2,M3, PL3,P3, PL3,  // 06
			M0,   0,P0, ILL,M1,   0,P1, ILL,M2,   0,P2,   0,M3,   0,P3,   0,
			M0,   0,P0,   0,M1,   0,P1,   0,M2,   0,P2,   0,M3,   0,P3,   0,
			 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0,
			M0,P000,P0,P000,M1,P100,P1,P100,M2,P200,P2,P200,M3,P300,P3,P300,
			M0,P001,P0,P001,M1,P101,P1,P101,M2,P201,P2,P201,M3,P301,P3,P301,
			M0,P010,P0,P010,M1,P110,P1,P110,M2,P210,P2,P210,M3,P310,P3,P310,
			M0,P011,P0,P011,M1,P111,P1,P111,M2,P211,P2,P211,M3,P311,P3,P311
		},
		{
			M0, PL0,P0, PL0,M1, PL1,P1, PL1,M2, PL2,P2, PL2,M3, PL3,P3, PL3,  // 07
			M0, ILL,P0, ILL,M1, ILL,P1, ILL,M2, PL2,P2, PL2,M3, PL3,P3, PL3,
			M0,   0,P0,   0,M1,   0,P1,   0,M2, ILL,P2, ILL,M3, ILL,P3, ILL,
			 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0,
			M0,P000,P0,P000,M1,P100,P1,P100,M2,P200,P2,P200,M3,P300,P3,P300,
			M0,P001,P0,P001,M1,P101,P1,P101,M2,P201,P2,P201,M3,P301,P3,P301,
			M0,P010,P0,P010,M1,P110,P1,P110,M2,P210,P2,P210,M3,P310,P3,P310,
			M0,P011,P0,P011,M1,P111,P1,P111,M2,P211,P2,P211,M3,P311,P3,P311
		},
		{
			M0, PL0,P0, PL0,M1, PL1,P1, PL1,M2, PL2,P2, PL2,M3, PL3,P3, PL3,  // 08
			M0,   0,P0,   0,M1,   0,P1,   0,M2,   0,P2,   0,M3,   0,P3,   0,
			M0, PL0,P0, PL0,M1, PL1,P1, PL1,M2, PL2,P2, PL2,M3, PL3,P3, PL3,
			 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0,
			M0,P000,P0,P000,M1,P100,P1,P100,M2,P200,P2,P200,M3,P300,P3,P300,
			M0,P001,P0,P001,M1,P101,P1,P101,M2,P201,P2,P201,M3,P301,P3,P301,
			M0,P010,P0,P010,M1,P110,P1,P110,M2,P210,P2,P210,M3,P310,P3,P310,
			M0,P011,P0,P011,M1,P111,P1,P111,M2,P211,P2,P211,M3,P311,P3,P311
		},
		{
			M0, PL0,P0, PL0,M1, PL1,P1, PL1,M2, PL2,P2, PL2,M3, PL3,P3, PL3,  // 09
			M0, ILL,P0, ILL,M1, ILL,P1, ILL,M2, PL2,P2, PL2,M3, PL3,P3, PL3,
			M0, PL0,P0, PL0,M1, PL1,P1, PL1,M2, PL2,P2, PL2,M3, PL3,P3, PL3,
			 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0,
			M0,P000,P0,P000,M1,P100,P1,P100,M2,P200,P2,P200,M3,P300,P3,P300,
			M0,P001,P0,P001,M1,P101,P1,P101,M2,P201,P2,P201,M3,P301,P3,P301,
			M0,P010,P0,P010,M1,P110,P1,P110,M2,P210,P2,P210,M3,P310,P3,P310,
			M0,P011,P0,P011,M1,P111,P1,P111,M2,P211,P2,P211,M3,P311,P3,P311
		},
		{
			M0, PL0,P0, PL0,M1, PL1,P1, PL1,M2, PL2,P2, PL2,M3, PL3,P3, PL3,  // 0A
			M0, ILL,P0, ILL,M1, ILL,P1, ILL,M2,   0,P2,   0,M3,   0,P3,   0,
			M0, PL0,P0, PL0,M1, PL1,P1, PL1,M2, ILL,P2, ILL,M3, ILL,P3, ILL,
			 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0,
			M0,P000,P0,P000,M1,P100,P1,P100,M2,P200,P2,P200,M3,P300,P3,P300,
			M0,P001,P0,P001,M1,P101,P1,P101,M2,P201,P2,P201,M3,P301,P3,P301,
			M0,P010,P0,P010,M1,P110,P1,P110,M2,P210,P2,P210,M3,P310,P3,P310,
			M0,P011,P0,P011,M1,P111,P1,P111,M2,P211,P2,P211,M3,P311,P3,P311
		},
		{
			M0, PL0,P0, PL0,M1, PL1,P1, PL1,M2, PL2,P2, PL2,M3, PL3,P3, PL3,  // 0B
			M0, ILL,P0, ILL,M1, ILL,P1, ILL,M2, PL2,P2, PL2,M3, PL3,P3, PL3,
			M0, PL0,P0, PL0,M1, PL1,P1, PL1,M2, ILL,P2, ILL,M3, ILL,P3, ILL,
			 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0,
			M0,P000,P0,P000,M1,P100,P1,P100,M2,P200,P2,P200,M3,P300,P3,P300,
			M0,P001,P0,P001,M1,P101,P1,P101,M2,P201,P2,P201,M3,P301,P3,P301,
			M0,P010,P0,P010,M1,P110,P1,P110,M2,P210,P2,P210,M3,P310,P3,P310,
			M0,P011,P0,P011,M1,P111,P1,P111,M2,P211,P2,P211,M3,P311,P3,P311
		},
		{
			M0, PL0,P0, PL0,M1, PL1,P1, PL1,M2, PL2,P2, PL2,M3, PL3,P3, PL3,  // 0C
			M0,   0,P0,   0,M1,   0,P1,   0,M2,   0,P2,   0,M3,   0,P3,   0,
			M0,   0,P0,   0,M1,   0,P1,   0,M2, ILL,P2, ILL,M3, ILL,P3, ILL,
			 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0,
			M0,P000,P0,P000,M1,P100,P1,P100,M2,P200,P2,P200,M3,P300,P3,P300,
			M0,P001,P0,P001,M1,P101,P1,P101,M2,P201,P2,P201,M3,P301,P3,P301,
			M0,P010,P0,P010,M1,P110,P1,P110,M2,P210,P2,P210,M3,P310,P3,P310,
			M0,P011,P0,P011,M1,P111,P1,P111,M2,P211,P2,P211,M3,P311,P3,P311
		},
		{
			M0, PL0,P0, PL0,M1, PL1,P1, PL1,M2, PL2,P2, PL2,M3, PL3,P3, PL3,  // 0D
			M0,   0,P0,   0,M1,   0,P1,   0,M2, PL2,P2, PL2,M3, PL3,P3, PL3,
			M0,   0,P0,   0,M1,   0,P1,   0,M2, ILL,P2, ILL,M3, ILL,P3, ILL,
			 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0,
			M0,P000,P0,P000,M1,P100,P1,P100,M2,P200,P2,P200,M3,P300,P3,P300,
			M0,P001,P0,P001,M1,P101,P1,P101,M2,P201,P2,P201,M3,P301,P3,P301,
			M0,P010,P0,P010,M1,P110,P1,P110,M2,P210,P2,P210,M3,P310,P3,P310,
			M0,P011,P0,P011,M1,P111,P1,P111,M2,P211,P2,P211,M3,P311,P3,P311
		},
		{
			M0, PL0,P0, PL0,M1, PL1,P1, PL1,M2, PL2,P2, PL2,M3, PL3,P3, PL3,  // 0E
			M0,   0,P0,   0,M1,   0,P1,   0,M2,   0,P2,   0,M3,   0,P3,   0,
			M0,   0,P0,   0,M1,   0,P1,   0,M2, ILL,P2, ILL,M3, ILL,P3, ILL,
			 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0,
			M0,P000,P0,P000,M1,P100,P1,P100,M2,P200,P2,P200,M3,P300,P3,P300,
			M0,P001,P0,P001,M1,P101,P1,P101,M2,P201,P2,P201,M3,P301,P3,P301,
			M0,P010,P0,P010,M1,P110,P1,P110,M2,P210,P2,P210,M3,P310,P3,P310,
			M0,P011,P0,P011,M1,P111,P1,P111,M2,P211,P2,P211,M3,P311,P3,P311
		},
		{
			M0, PL0,P0, PL0,M1, PL1,P1, PL1,M2, PL2,P2, PL2,M3, PL3,P3, PL3,  // 0F
			M0,   0,P0,   0,M1,   0,P1,   0,M2, PL2,P2, PL2,M3, PL3,P3, PL3,
			M0,   0,P0,   0,M1,   0,P1,   0,M2, ILL,P2, ILL,M3, ILL,P3, ILL,
			 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0,
			M0,P000,P0,P000,M1,P100,P1,P100,M2,P200,P2,P200,M3,P300,P3,P300,
			M0,P001,P0,P001,M1,P101,P1,P101,M2,P201,P2,P201,M3,P301,P3,P301,
			M0,P010,P0,P010,M1,P110,P1,P110,M2,P210,P2,P210,M3,P310,P3,P310,
			M0,P011,P0,P011,M1,P111,P1,P111,M2,P211,P2,P211,M3,P311,P3,P311
		},
		{
			P0, PL0,P1, PL1,P2, PL2,P3, PL3,M0, PM4,M1, PM4,M2, PM4,M3, PM4,  // 10
			P0, PL0,P1, PL1,P2, PL2,P3, PL3,M0, PM4,M1, PM4,M2, PM4,M3, PM4,
			P0, PL0,P1, PL1,P2, PL2,P3, PL3,M0, PM4,M1, PM4,M2, PM4,M3, PM4,
			 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0,
			P0,P000,P1,P100,P2,P200,P3,P300,M0,P400,M1,P400,M2,P400,M3,P400,
			P0,P001,P1,P101,P2,P201,P3,P301,M0,P401,M1,P401,M2,P401,M3,P401,
			P0,P010,P1,P110,P2,P210,P3,P310,M0,P410,M1,P410,M2,P410,M3,P410,
			P0,P011,P1,P111,P2,P211,P3,P311,M0,P411,M1,P411,M2,P411,M3,P411
		},
		{
			P0, PL0,P1, PL1,P2, PL2,P3, PL3,M0, PM4,M1, PM4,M2, PM4,M3, PM4,  // 11
			P0, PL0,P1, PL1,P2, PL2,P3, PL3,M0, PM4,M1, PM4,M2, PM4,M3, PM4,
			P0, PL0,P1, PL1,P2, PL2,P3, PL3,M0, PM4,M1, PM4,M2, PM4,M3, PM4,
			 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0,
			P0,P000,P1,P100,P2,P200,P3,P300,M0,P400,M1,P400,M2,P400,M3,P400,
			P0,P001,P1,P101,P2,P201,P3,P301,M0,P401,M1,P401,M2,P401,M3,P401,
			P0,P010,P1,P110,P2,P210,P3,P310,M0,P410,M1,P410,M2,P410,M3,P410,
			P0,P011,P1,P111,P2,P211,P3,P311,M0,P411,M1,P411,M2,P411,M3,P411
		},
		{
			P0, PL0,P1, PL1,M0, PM4,M1, PM4,M2, PM4,M3, PM4,P2, PL2,P3, PL3,  // 12
			P0, PL0,P1, PL1,M0, PM4,M1, PM4,M2, PM4,M3, PM4,P2,   0,P3,   0,
			P0, PL0,P1, PL1,M0, PM4,M1, PM4,M2, PM4,M3, PM4,P2,   0,P3,   0,
			 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0,
			P0,P000,P1,P100,M0,P400,M1,P400,M2,P400,M3,P400,P2,P200,P3,P300,
			P0,P001,P1,P101,M0,P401,M1,P401,M2,P401,M3,P401,P2,P201,P3,P301,
			P0,P010,P1,P110,M0,P410,M1,P410,M2,P410,M3,P410,P2,P210,P3,P310,
			P0,P011,P1,P111,M0,P411,M1,P411,M2,P411,M3,P411,P2,P211,P3,P311
		},
		{
			P0, PL0,P1, PL1,M0, PM4,M1, PM4,M2, PM4,M3, PM4,P2, PL2,P3, PL3,  // 13
			P0, PL0,P1, PL1,M0, PM4,M1, PM4,M2, PM4,M3, PM4,P2, PL2,P3, PL3,
			P0, PL0,P1, PL1,M0, PM4,M1, PM4,M2, PM4,M3, PM4,P2, ILL,P3, ILL,
			 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0,
			P0,P000,P1,P100,M0,P400,M1,P400,M2,P400,M3,P400,P2,P200,P3,P300,
			P0,P001,P1,P101,M0,P401,M1,P401,M2,P401,M3,P401,P2,P201,P3,P301,
			P0,P010,P1,P110,M0,P410,M1,P410,M2,P410,M3,P410,P2,P210,P3,P310,
			P0,P011,P1,P111,M0,P411,M1,P411,M2,P411,M3,P411,P2,P211,P3,P311
		},
		{
			M0, PM4,M1, PM4,M2, PM4,M3, PM4,P0, PL0,P1, PL1,P2, PL2,P3, PL3,  // 14
			M0, PM4,M1, PM4,M2, PM4,M3, PM4,P0,   0,P1,   0,P2,   0,P3,   0,
			M0, PM4,M1, PM4,M2, PM4,M3, PM4,P0,   0,P1,   0,P2,   0,P3,   0,
			 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0,
			M0,P400,M1,P400,M2,P400,M3,P400,P0,P000,P1,P100,P2,P200,P3,P300,
			M0,P401,M1,P401,M2,P401,M3,P401,P0,P001,P1,P101,P2,P201,P3,P301,
			M0,P410,M1,P410,M2,P410,M3,P410,P0,P010,P1,P110,P2,P210,P3,P310,
			M0,P411,M1,P411,M2,P411,M3,P411,P0,P011,P1,P111,P2,P211,P3,P311
		},
		{
			M0, PM4,M1, PM4,M2, PM4,M3, PM4,P2,P0, PL0,P1, PL1, PL2,P3, PL3,  // 15
			M0, PM4,M1, PM4,M2, PM4,M3, PM4,P2,P0, ILL,P1, ILL, PL2,P3, PL3,
			M0, PM4,M1, PM4,M2, PM4,M3, PM4,P2,P0,	 0,P1,	 0, ILL,P3, ILL,
			 0,   0, 0,   0, 0,   0, 0,   0, 0, 0,	 0, 0,	 0,   0, 0,   0,
			M0,P000,M1,P100,M2,P200,M3,P300,P2,P0,P000,P1,P100,P200,P3,P300,
			M0,P001,M1,P101,M2,P201,M3,P301,P2,P0,P001,P1,P101,P201,P3,P301,
			M0,P010,M1,P110,M2,P210,M3,P310,P2,P0,P010,P1,P110,P210,P3,P310,
			M0,P011,M1,P111,M2,P211,M3,P311,P2,P0,P011,P1,P111,P211,P3,P311
		},
		{
			M0, PM4,M1, PM4,M2, PM4,M3, PM4,P0, PL0,P1, PL1,P2, PL2,P3, PL3,  // 16
			M0, PM4,M1, PM4,M2, PM4,M3, PM4,P0, ILL,P1, ILL,P2,   0,P3,   0,
			M0, PM4,M1, PM4,M2, PM4,M3, PM4,P0,   0,P1,   0,P2,   0,P3,   0,
			 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0,
			M0,P000,M1,P100,M2,P200,M3,P300,P0,P000,P1,P100,P2,P200,P3,P300,
			M0,P001,M1,P101,M2,P201,M3,P301,P0,P001,P1,P101,P2,P201,P3,P301,
			M0,P010,M1,P110,M2,P210,M3,P310,P0,P010,P1,P110,P2,P210,P3,P310,
			M0,P011,M1,P111,M2,P211,M3,P311,P0,P011,P1,P111,P2,P211,P3,P311
		},
		{
			M0, PM4,M1, PM4,M2, PM4,M3, PM4,P2,P0, PL0,P1, PL1, PL2,P3, PL3,  // 17
			M0, PM4,M1, PM4,M2, PM4,M3, PM4,P2,P0, ILL,P1, ILL, PL2,P3, PL3,
			M0, PM4,M1, PM4,M2, PM4,M3, PM4,P2,P0,	 0,P1,	 0, ILL,P3, ILL,
			 0,   0, 0,   0, 0,   0, 0,   0, 0, 0,	 0, 0,	 0,   0, 0,   0,
			M0,P000,M1,P100,M2,P200,M3,P300,P2,P0,P000,P1,P100,P200,P3,P300,
			M0,P001,M1,P101,M2,P201,M3,P301,P2,P0,P001,P1,P101,P201,P3,P301,
			M0,P010,M1,P110,M2,P210,M3,P310,P2,P0,P010,P1,P110,P210,P3,P310,
			M0,P011,M1,P111,M2,P211,M3,P311,P2,P0,P011,P1,P111,P211,P3,P311
		},
		{
			P0, PL0,P1, PL1,P2, PL2,P3, PL3,M0, PM4,M1, PM4,M2, PM4,M3, PM4,  // 18
			P0, PL0,P1, PL1,P2, PL2,P3, PL3,M0, PM4,M1, PM4,M2, PM4,M3, PM4,
			P0, PL0,P1, PL1,P2, PL2,P3, PL3,M0, PM4,M1, PM4,M2, PM4,M3, PM4,
			 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0,
			P0,P000,P1,P100,P2,P200,P3,P300,M0,P400,M1,P400,M2,P400,M3,P400,
			P0,P001,P1,P101,P2,P201,P3,P301,M0,P401,M1,P401,M2,P401,M3,P401,
			P0,P010,P1,P110,P2,P210,P3,P310,M0,P410,M1,P410,M2,P410,M3,P410,
			P0,P011,P1,P111,P2,P211,P3,P311,M0,P411,M1,P411,M2,P411,M3,P411
		},
		{
			P0, PL0,P1, PL1,P2, PL2,P3, PL3,M0, PM4,M1, PM4,M2, PM4,M3, PM4,  // 19
			P0, ILL,P1, ILL,P2, PL2,P3, PL3,M0, PM4,M1, PM4,M2, PM4,M3, PM4,
			P0, PL0,P1, PL1,P2, PL2,P3, PL3,M0, PM4,M1, PM4,M2, PM4,M3, PM4,
			 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0,
			P0,P000,P1,P100,P2,P200,P3,P300,M0,P000,M1,P100,M2,P200,M3,P300,
			P0,P001,P1,P101,P2,P201,P3,P301,M0,P001,M1,P101,M2,P201,M3,P301,
			P0,P010,P1,P110,P2,P210,P3,P310,M0,P010,M1,P110,M2,P210,M3,P310,
			P0,P011,P1,P111,P2,P211,P3,P311,M0,P011,M1,P111,M2,P211,M3,P311
		},
		{
			P0, PL0,P1, PL1,P2, PL2,P3, PL3,M0, PM4,M1, PM4,M2, PM4,M3, PM4,  // 1A
			P0, ILL,P1, ILL,P2,   0,P3,   0,M0, PM4,M1, PM4,M2, PM4,M3, PM4,
			P0, PL0,P1, PL1,P2, ILL,P3, ILL,M0, PM4,M1, PM4,M2, PM4,M3, PM4,
			 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0,
			P0,P000,P1,P100,P2,P200,P3,P300,M0,P400,M1,P400,M2,P400,M3,P400,
			P0,P001,P1,P101,P2,P201,P3,P301,M0,P401,M1,P401,M2,P401,M3,P401,
			P0,P010,P1,P110,P2,P210,P3,P310,M0,P410,M1,P410,M2,P410,M3,P410,
			P0,P011,P1,P111,P2,P211,P3,P311,M0,P411,M1,P411,M2,P411,M3,P411
		},
		{
			P0, PL0,P1, PL1,P2, PL2,P3, PL3,M0, PM4,M1, PM4,M2, PM4,M3, PM4,  // 1B
			P0, ILL,P1, ILL,P2, PL2,P3, PL3,M0, PM4,M1, PM4,M2, PM4,M3, PM4,
			P0, PL0,P1, PL1,P2, ILL,P3, ILL,M0, PM4,M1, PM4,M2, PM4,M3, PM4,
			 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0,
			P0,P000,P1,P100,P2,P200,P3,P300,M0,P400,M1,P400,M2,P400,M3,P400,
			P0,P001,P1,P101,P2,P201,P3,P301,M0,P401,M1,P401,M2,P401,M3,P401,
			P0,P010,P1,P110,P2,P210,P3,P310,M0,P410,M1,P410,M2,P410,M3,P410,
			P0,P011,P1,P111,P2,P211,P3,P311,M0,P411,M1,P411,M2,P411,M3,P411
		},
		{
			P0, PL0,P1, PL1,P2, PL2,P3, PL3,M0, PM4,M1, PM4,M2, PM4,M3, PM4,  // 1C
			P0,   0,P1,   0,P2,   0,P3,   0,M0, PM4,M1, PM4,M2, PM4,M3, PM4,
			P0,   0,P1,   0,P2, ILL,P3, ILL,M0, PM4,M1, PM4,M2, PM4,M3, PM4,
			 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0,
			P0,P000,P1,P100,P2,P200,P3,P300,M0,P400,M1,P400,M2,P400,M3,P400,
			P0,P001,P1,P101,P2,P201,P3,P301,M0,P401,M1,P401,M2,P401,M3,P401,
			P0,P010,P1,P110,P2,P210,P3,P310,M0,P410,M1,P410,M2,P410,M3,P410,
			P0,P011,P1,P111,P2,P211,P3,P311,M0,P411,M1,P411,M2,P411,M3,P411
		},
		{
			P0, PL0,P1, PL1,P2, PL2,P3, PL3,M0, PM4,M1, PM4,M2, PM4,M3, PM4,  // 1D
			P0,   0,P1,   0,P2, PL2,P3, PL3,M0, PM4,M1, PM4,M2, PM4,M3, PM4,
			P0,   0,P1,   0,P2, ILL,P3, ILL,M0, PM4,M1, PM4,M2, PM4,M3, PM4,
			 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0,
			P0,P000,P1,P100,P2,P200,P3,P300,M0,P400,M1,P400,M2,P400,M3,P400,
			P0,P001,P1,P101,P2,P201,P3,P301,M0,P401,M1,P401,M2,P401,M3,P401,
			P0,P010,P1,P110,P2,P210,P3,P310,M0,P410,M1,P410,M2,P410,M3,P410,
			P0,P011,P1,P111,P2,P211,P3,P311,M0,P411,M1,P411,M2,P411,M3,P411
		},
		{
			P0, PL0,P1, PL1,P2, PL2,P3, PL3,M0, PM4,M1, PM4,M2, PM4,M3, PM4,  // 1E
			P0,   0,P1,   0,P2,   0,P3,   0,M0, PM4,M1, PM4,M2, PM4,M3, PM4,
			P0,   0,P1,   0,P2, ILL,P3, ILL,M0, PM4,M1, PM4,M2, PM4,M3, PM4,
			 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0,
			P0,P000,P1,P100,P2,P200,P3,P300,M0,P400,M1,P400,M2,P400,M3,P400,
			P0,P001,P1,P101,P2,P201,P3,P301,M0,P401,M1,P401,M2,P401,M3,P401,
			P0,P010,P1,P110,P2,P210,P3,P310,M0,P410,M1,P410,M2,P410,M3,P410,
			P0,P011,P1,P111,P2,P211,P3,P311,M0,P411,M1,P411,M2,P411,M3,P411
		},
		{
			P0, PL0,P1, PL1,P2, PL2,P3, PL3,M0, PM4,M1, PM4,M2, PM4,M3, PM4,  // 1F
			P0,   0,P1,   0,P2,   0,P3,   0,M0, PM4,M1, PM4,M2, PM4,M3, PM4,
			P0,   0,P1,   0,P2, ILL,P3, ILL,M0, PM4,M1, PM4,M2, PM4,M3, PM4,
			 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0, 0,   0,
			P0,P000,P1,P100,P2,P200,P3,P300,M0,P400,M1,P400,M2,P400,M3,P400,
			P0,P001,P1,P101,P2,P201,P3,P301,M0,P401,M1,P401,M2,P401,M3,P401,
			P0,P010,P1,P110,P2,P210,P3,P310,M0,P410,M1,P410,M2,P410,M3,P410,
			P0,P011,P1,P111,P2,P211,P3,P311,M0,P411,M1,P411,M2,P411,M3,P411
		}
	};
	
	/************************************************************************
	 * prio_init
	 * Initialize player/missile priority lookup tables
	 ************************************************************************/
	static void prio_init(void)
	{
		int i, j, pm, p, c;
		UINT8 * prio;
	
		/* 32 priority bit combinations */
		for( i = 0; i < 32; i++ )
		{
			/* 8 playfield colors */
			for( j = 0; j < 8; j++ )
			{
				prio = &_pm_colors[i][j*16];
				/* 256 player/missile combinations to build */
				for( pm = 0; pm < 256; pm++ )
				{
					c = PFD; /* assume playfield color */
					for( p = 0; (c == PFD) && (p < 16); p += 2 )
					{
						if (((prio[p] & pm) == prio[p]) && (prio[p+1]))
							c = prio[p+1];
					}
					antic.prio_table[i][(j << 8) + pm] = c;
					if( (c==PL0 || c==P000 || c==P001 || c==P010 || c==P011) &&
						(pm & (P0+P1))==(P0+P1))
						c = EOR;
					if( (c==PL2 || c==P200 || c==P201 || c==P210 || c==P211) &&
						(pm & (P2+P3))==(P2+P3))
						c = EOR;
					antic.prio_table[32 + i][(j << 8) + pm] = c;
				}
			}
		}
	}
	
	/************************************************************************
	 * cclk_init
	 * Initialize "color clock" lookup tables
	 ************************************************************************/
	static void cclk_init(void)
	{
		static UINT8 _pf_21[4] =   {T00,T01,T10,T11};
		static UINT8 _pf_1b[4] =   {G00,G01,G10,G11};
		static UINT8 _pf_210b[4] = {PBK,PF0,PF1,PF2};
		static UINT8 _pf_310b[4] = {PBK,PF0,PF1,PF3};
		int i;
		UINT8 * dst;
	
		/* setup color translation for the ANTIC modes */
	    for( i = 0; i < 256; i++ )
	    {
	        /****** text mode (2,3) **********/
			dst = (UINT8 *)&antic.pf_21[0x000+i];
			*dst++ = _pf_21[(i>>6)&3];
			*dst++ = _pf_21[(i>>4)&3];
			*dst++ = _pf_21[(i>>2)&3];
			*dst++ = _pf_21[(i>>0)&3];
	
	        /****** 4 color text (4,5) with pf2, D, E **********/
			dst = (UINT8 *)&antic.pf_x10b[0x000+i];
			*dst++ = _pf_210b[(i>>6)&3];
			*dst++ = _pf_210b[(i>>4)&3];
			*dst++ = _pf_210b[(i>>2)&3];
			*dst++ = _pf_210b[(i>>0)&3];
			dst = (UINT8 *)&antic.pf_x10b[0x100+i];
			*dst++ = _pf_310b[(i>>6)&3];
			*dst++ = _pf_310b[(i>>4)&3];
			*dst++ = _pf_310b[(i>>2)&3];
			*dst++ = _pf_310b[(i>>0)&3];
	
	        /****** pf0 color text (6,7), 9, B, C **********/
			dst = (UINT8 *)&antic.pf_3210b2[0x000+i*2];
			*dst++ = (i&0x80)?PF0:PBK;
			*dst++ = (i&0x40)?PF0:PBK;
			*dst++ = (i&0x20)?PF0:PBK;
			*dst++ = (i&0x10)?PF0:PBK;
			*dst++ = (i&0x08)?PF0:PBK;
			*dst++ = (i&0x04)?PF0:PBK;
			*dst++ = (i&0x02)?PF0:PBK;
			*dst++ = (i&0x01)?PF0:PBK;
	
	        /****** pf1 color text (6,7), 9, B, C **********/
			dst = (UINT8 *)&antic.pf_3210b2[0x200+i*2];
			*dst++ = (i&0x80)?PF1:PBK;
			*dst++ = (i&0x40)?PF1:PBK;
			*dst++ = (i&0x20)?PF1:PBK;
			*dst++ = (i&0x10)?PF1:PBK;
			*dst++ = (i&0x08)?PF1:PBK;
			*dst++ = (i&0x04)?PF1:PBK;
			*dst++ = (i&0x02)?PF1:PBK;
			*dst++ = (i&0x01)?PF1:PBK;
	
	        /****** pf2 color text (6,7), 9, B, C **********/
			dst = (UINT8 *)&antic.pf_3210b2[0x400+i*2];
			*dst++ = (i&0x80)?PF2:PBK;
			*dst++ = (i&0x40)?PF2:PBK;
			*dst++ = (i&0x20)?PF2:PBK;
			*dst++ = (i&0x10)?PF2:PBK;
			*dst++ = (i&0x08)?PF2:PBK;
			*dst++ = (i&0x04)?PF2:PBK;
			*dst++ = (i&0x02)?PF2:PBK;
			*dst++ = (i&0x01)?PF2:PBK;
	
	        /****** pf3 color text (6,7), 9, B, C **********/
			dst = (UINT8 *)&antic.pf_3210b2[0x600+i*2];
			*dst++ = (i&0x80)?PF3:PBK;
			*dst++ = (i&0x40)?PF3:PBK;
			*dst++ = (i&0x20)?PF3:PBK;
			*dst++ = (i&0x10)?PF3:PBK;
			*dst++ = (i&0x08)?PF3:PBK;
			*dst++ = (i&0x04)?PF3:PBK;
			*dst++ = (i&0x02)?PF3:PBK;
			*dst++ = (i&0x01)?PF3:PBK;
	
	        /****** 4 color graphics 4 cclks (8) **********/
			dst = (UINT8 *)&antic.pf_210b4[i*4];
			*dst++ = _pf_210b[(i>>6)&3];
			*dst++ = _pf_210b[(i>>6)&3];
			*dst++ = _pf_210b[(i>>6)&3];
			*dst++ = _pf_210b[(i>>6)&3];
			*dst++ = _pf_210b[(i>>4)&3];
			*dst++ = _pf_210b[(i>>4)&3];
			*dst++ = _pf_210b[(i>>4)&3];
			*dst++ = _pf_210b[(i>>4)&3];
			*dst++ = _pf_210b[(i>>2)&3];
			*dst++ = _pf_210b[(i>>2)&3];
			*dst++ = _pf_210b[(i>>2)&3];
			*dst++ = _pf_210b[(i>>2)&3];
			*dst++ = _pf_210b[(i>>0)&3];
			*dst++ = _pf_210b[(i>>0)&3];
			*dst++ = _pf_210b[(i>>0)&3];
			*dst++ = _pf_210b[(i>>0)&3];
	
	        /****** 4 color graphics 2 cclks (A) **********/
			dst = (UINT8 *)&antic.pf_210b2[i*2];
			*dst++ = _pf_210b[(i>>6)&3];
			*dst++ = _pf_210b[(i>>6)&3];
			*dst++ = _pf_210b[(i>>4)&3];
			*dst++ = _pf_210b[(i>>4)&3];
			*dst++ = _pf_210b[(i>>2)&3];
			*dst++ = _pf_210b[(i>>2)&3];
			*dst++ = _pf_210b[(i>>0)&3];
			*dst++ = _pf_210b[(i>>0)&3];
	
	        /****** high resolution graphics (F) **********/
			dst = (UINT8 *)&antic.pf_1b[i];
			*dst++ = _pf_1b[(i>>6)&3];
			*dst++ = _pf_1b[(i>>4)&3];
			*dst++ = _pf_1b[(i>>2)&3];
			*dst++ = _pf_1b[(i>>0)&3];
	
	        /****** gtia mode 1 **********/
			dst = (UINT8 *)&antic.pf_gtia1[i];
			*dst++ = GT1+((i>>4)&15);
			*dst++ = GT1+((i>>4)&15);
			*dst++ = GT1+(i&15);
			*dst++ = GT1+(i&15);
	
	        /****** gtia mode 2 **********/
			dst = (UINT8 *)&antic.pf_gtia2[i];
			*dst++ = GT2+((i>>4)&15);
			*dst++ = GT2+((i>>4)&15);
			*dst++ = GT2+(i&15);
			*dst++ = GT2+(i&15);
	
	        /****** gtia mode 3 **********/
			dst = (UINT8 *)&antic.pf_gtia3[i];
			*dst++ = GT3+((i>>4)&15);
			*dst++ = GT3+((i>>4)&15);
			*dst++ = GT3+(i&15);
			*dst++ = GT3+(i&15);
	
	    }
	
		/* setup used color tables */
	    for( i = 0; i < 256; i++ )
		{
			/* used colors in text modes 2,3 */
			antic.uc_21[i] = (i) ? PF2 | PF1 : PF2;
	
			/* used colors in text modes 4,5 and graphics modes D,E */
			switch( i & 0x03 )
			{
				case 0x01: antic.uc_x10b[0x000+i] |= PF0; antic.uc_x10b[0x100+i] |= PF0; break;
				case 0x02: antic.uc_x10b[0x000+i] |= PF1; antic.uc_x10b[0x100+i] |= PF1; break;
				case 0x03: antic.uc_x10b[0x000+i] |= PF2; antic.uc_x10b[0x100+i] |= PF3; break;
			}
			switch( i & 0x0c )
			{
				case 0x04: antic.uc_x10b[0x000+i] |= PF0; antic.uc_x10b[0x100+i] |= PF0; break;
				case 0x08: antic.uc_x10b[0x000+i] |= PF1; antic.uc_x10b[0x100+i] |= PF1; break;
				case 0x0c: antic.uc_x10b[0x000+i] |= PF2; antic.uc_x10b[0x100+i] |= PF3; break;
	        }
			switch( i & 0x30 )
			{
				case 0x10: antic.uc_x10b[0x000+i] |= PF0; antic.uc_x10b[0x100+i] |= PF0; break;
				case 0x20: antic.uc_x10b[0x000+i] |= PF1; antic.uc_x10b[0x100+i] |= PF1; break;
				case 0x30: antic.uc_x10b[0x000+i] |= PF2; antic.uc_x10b[0x100+i] |= PF3; break;
			}
			switch( i & 0xc0 )
			{
				case 0x40: antic.uc_x10b[0x000+i] |= PF0; antic.uc_x10b[0x100+i] |= PF0; break;
				case 0x80: antic.uc_x10b[0x000+i] |= PF1; antic.uc_x10b[0x100+i] |= PF1; break;
				case 0xc0: antic.uc_x10b[0x000+i] |= PF2; antic.uc_x10b[0x100+i] |= PF3; break;
	        }
	
			/* used colors in text modes 6,7 and graphics modes 9,B,C */
			if (i != 0)
			{
				antic.uc_3210b2[0x000+i*2] |= PF0;
				antic.uc_3210b2[0x200+i*2] |= PF1;
				antic.uc_3210b2[0x400+i*2] |= PF2;
				antic.uc_3210b2[0x600+i*2] |= PF3;
	        }
	
			/* used colors in graphics mode 8 */
			switch( i & 0x03 )
			{
				case 0x01: antic.uc_210b4[i*4] |= PF0; break;
				case 0x02: antic.uc_210b4[i*4] |= PF1; break;
				case 0x03: antic.uc_210b4[i*4] |= PF2; break;
			}
			switch( i & 0x0c )
			{
				case 0x04: antic.uc_210b4[i*4] |= PF0; break;
				case 0x08: antic.uc_210b4[i*4] |= PF1; break;
				case 0x0c: antic.uc_210b4[i*4] |= PF2; break;
	        }
			switch( i & 0x30 )
			{
				case 0x10: antic.uc_210b4[i*4] |= PF0; break;
				case 0x20: antic.uc_210b4[i*4] |= PF1; break;
				case 0x30: antic.uc_210b4[i*4] |= PF2; break;
			}
			switch( i & 0xc0 )
			{
				case 0x40: antic.uc_210b4[i*4] |= PF0; break;
				case 0x80: antic.uc_210b4[i*4] |= PF1; break;
				case 0xc0: antic.uc_210b4[i*4] |= PF2; break;
	        }
	
			/* used colors in graphics mode A */
			switch( i & 0x03 )
			{
				case 0x01: antic.uc_210b2[i*2] |= PF0; break;
				case 0x02: antic.uc_210b2[i*2] |= PF1; break;
				case 0x03: antic.uc_210b2[i*2] |= PF2; break;
			}
			switch( i & 0x0c )
			{
				case 0x04: antic.uc_210b2[i*2] |= PF0; break;
				case 0x08: antic.uc_210b2[i*2] |= PF1; break;
				case 0x0c: antic.uc_210b2[i*2] |= PF2; break;
	        }
			switch( i & 0x30 )
			{
				case 0x10: antic.uc_210b2[i*2] |= PF0; break;
				case 0x20: antic.uc_210b2[i*2] |= PF1; break;
				case 0x30: antic.uc_210b2[i*2] |= PF2; break;
			}
			switch( i & 0xc0 )
			{
				case 0x40: antic.uc_210b2[i*2] |= PF0; break;
				case 0x80: antic.uc_210b2[i*2] |= PF1; break;
				case 0xc0: antic.uc_210b2[i*2] |= PF2; break;
	        }
	
			/* used colors in graphics mode F */
			if (i != 0)
				antic.uc_1b[i] |= PF1;
	
			/* used colors in GTIA graphics modes */
			/* GTIA 1 is 16 different luminances with hue of colbk */
			antic.uc_g1[i] = 0x00;
			/* GTIA 2 is all 9 colors (8..15 is colbk) */
			switch( i & 0x0f )
			{
				case 0x00: antic.uc_g2[i] = 0x10; break;
				case 0x01: antic.uc_g2[i] = 0x20; break;
				case 0x02: antic.uc_g2[i] = 0x40; break;
				case 0x03: antic.uc_g2[i] = 0x80; break;
				case 0x04: antic.uc_g2[i] = 0x01; break;
				case 0x05: antic.uc_g2[i] = 0x02; break;
				case 0x06: antic.uc_g2[i] = 0x04; break;
				case 0x07: antic.uc_g2[i] = 0x08; break;
				default:   antic.uc_g2[i] = 0x00;
	        }
	
	        /* GTIA 3 is 16 different hues with luminance of colbk */
			antic.uc_g3[i] = 0x00;
	    }
	}
	
	/************************************************************************
	 * atari_vh_start
	 * Initialize the ATARI800 video emulation
	 ************************************************************************/
	public static VhStartPtr atari_vh_start = new VhStartPtr() { public int handler() 
	{
		int i;
	
		LOG((errorlog, "atari antic_vh_start\n"));
	    memset(&antic, 0, sizeof(antic));
		memset(&gtia, 0, sizeof(gtia));
	
		antic.cclk_expand = malloc(21 * 256 * sizeof(UINT32));
		if( !antic.cclk_expand )
			return 1;
	
		antic.pf_21 	  = &antic.cclk_expand[ 0 * 256];
		antic.pf_x10b	  = &antic.cclk_expand[ 1 * 256];
		antic.pf_3210b2   = &antic.cclk_expand[ 3 * 256];
		antic.pf_210b4	  = &antic.cclk_expand[11 * 256];
		antic.pf_210b2	  = &antic.cclk_expand[15 * 256];
		antic.pf_1b 	  = &antic.cclk_expand[17 * 256];
		antic.pf_gtia1	  = &antic.cclk_expand[18 * 256];
		antic.pf_gtia2	  = &antic.cclk_expand[19 * 256];
		antic.pf_gtia3	  = &antic.cclk_expand[20 * 256];
	
		antic.used_colors = malloc(21 * 256 * sizeof(UINT8));
		if( !antic.used_colors )
		{
			free(antic.cclk_expand);
			return 1;
		}
		memset(antic.used_colors, 0, 21 * 256 * sizeof(UINT8));
	
		antic.uc_21 	  = &antic.used_colors[ 0 * 256];
		antic.uc_x10b	  = &antic.used_colors[ 1 * 256];
		antic.uc_3210b2   = &antic.used_colors[ 3 * 256];
		antic.uc_210b4	  = &antic.used_colors[11 * 256];
		antic.uc_210b2	  = &antic.used_colors[15 * 256];
		antic.uc_1b 	  = &antic.used_colors[17 * 256];
		antic.uc_g1 	  = &antic.used_colors[18 * 256];
		antic.uc_g2 	  = &antic.used_colors[19 * 256];
		antic.uc_g3 	  = &antic.used_colors[20 * 256];
	
		/* reset the ANTIC color tables */
		for( i = 0; i < 256; i ++ )
	        antic.color_lookup[i] = (Machine.pens[0] << 8) + Machine.pens[0];
	
		LOG((errorlog, "atari cclk_init\n"));
	    cclk_init();
	
		for( i = 0; i < 64; i++ )
	    {
			antic.prio_table[i] = malloc(8*256);
			if( !antic.prio_table[i] )
			{
				while( --i >= 0 )
					free(antic.prio_table[i]);
				return 1;
			}
	    }
	
		LOG((errorlog, "atari prio_init\n"));
	    prio_init();
	
		for( i = 0; i < Machine.drv.screen_height; i++ )
	    {
			antic.video[i] = malloc(sizeof(VIDEO));
			if( !antic.video[i] )
	        {
				while( --i >= 0 )
					free(antic.video[i]);
	            return 1;
	        }
			memset(antic.video[i], 0, sizeof(VIDEO));
	    }
	
	    return 0;
	} };
	
	/************************************************************************
	 * atari_vh_stop
	 * Shutdown the ATARI800 video emulation
	 ************************************************************************/
	public static VhStopPtr atari_vh_stop = new VhStopPtr() { public void handler() 
	{
		int i;
	
		for( i = 0; i < 64; i++ )
		{
			if (antic.prio_table[i])
			{
				free(antic.prio_table[i]);
				antic.prio_table[i] = 0;
			}
		}
		for( i = 0; i < Machine.drv.screen_height; i++ )
		{
			if (antic.video[i])
			{
				free(antic.video[i]);
				antic.video[i] = 0;
			}
		}
	
		if( antic.cclk_expand )
		{
			free(antic.cclk_expand);
			antic.cclk_expand = 0;
		}
	} };
	
	/************************************************************************
	 * atari_vh_screenrefresh
	 * Refresh screen bitmap.
	 * Note: Actual drawing is done scanline wise during atari_interrupt
	 ************************************************************************/
	public static VhUpdatePtr atari_vh_screenrefresh = new VhUpdatePtr() { public void handler(osd_bitmap bitmap,int full_refresh) 
	{
		if( tv_artifacts != (readinputport(0) & 0x40) )
		{
			tv_artifacts = readinputport(0) & 0x40;
			full_refresh = 1;
		}
		if( atari_frame_counter > 0 )
		{
			if( --atari_frame_counter == 0 )
				full_refresh = 1;
			else
				ui_text(bitmap, atari_frame_message, 0, Machine.uiheight - 10);
		}
	    if (full_refresh != 0)
			fillbitmap(Machine.scrbitmap, Machine.pens[0], &Machine.visible_area);
	} };
	
	static renderer_function antic_renderer = antic_mode_0_xx;
	
	static void artifacts_gfx(UINT8 *src, UINT8 *dst, int width)
	{
		int x;
		UINT8 n, bits = 0;
		UINT8 b = gtia.w.colbk & 0xf0;
		UINT8 c = gtia.w.colpf1 & 0x0f;
		UINT8 _A = Machine.remapped_colortable[((b+0x30)&0xf0)+c];
		UINT8 _B = Machine.remapped_colortable[((b+0x70)&0xf0)+c];
		UINT8 _C = Machine.remapped_colortable[b+c];
		UINT8 _D = Machine.remapped_colortable[gtia.w.colbk];
	
		for( x = 0; x < width * 4; x++ )
		{
			n = *src++;
			bits <<= 2;
			switch( n )
			{
			case G00:
				break;
			case G01:
				bits |= 1;
				break;
			case G10:
				bits |= 2;
				break;
			case G11:
				bits |= 3;
				break;
			default:
				*dst++ = antic.color_lookup[n];
				*dst++ = antic.color_lookup[n];
				continue;
			}
			switch( (bits >> 1) & 7 )
			{
			case 0: /* 0 0 0 */
			case 1: /* 0 0 1 */
			case 4: /* 1 0 0 */
				*dst++ = _D;
				break;
			case 3: /* 0 1 1 */
			case 6: /* 1 1 0 */
			case 7: /* 1 1 1 */
				*dst++ = _C;
				break;
			case 2: /* 0 1 0 */
				*dst++ = _B;
				break;
			case 5: /* 1 0 1 */
				*dst++ = _A;
				break;
			}
			switch( bits & 7 )
			{
			case 0: /* 0 0 0 */
			case 1: /* 0 0 1 */
			case 4: /* 1 0 0 */
				*dst++ = _D;
				break;
			case 3: /* 0 1 1 */
			case 6: /* 1 1 0 */
			case 7: /* 1 1 1 */
				*dst++ = _C;
				break;
			case 2: /* 0 1 0 */
				*dst++ = _A;
				break;
			case 5: /* 1 0 1 */
				*dst++ = _B;
				break;
	        }
	    }
	}
	
	static void artifacts_txt(UINT8 * src, UINT8 * dst, int width)
	{
		int x;
		UINT8 n, bits = 0;
		UINT8 b = gtia.w.colpf2 & 0xf0;
		UINT8 c = gtia.w.colpf1 & 0x0f;
		UINT8 _A = Machine.remapped_colortable[((b+0x30)&0xf0)+c];
		UINT8 _B = Machine.remapped_colortable[((b+0x70)&0xf0)+c];
		UINT8 _C = Machine.remapped_colortable[b+c];
		UINT8 _D = Machine.remapped_colortable[gtia.w.colpf2];
	
		for( x = 0; x < width * 4; x++ )
		{
			n = *src++;
			bits <<= 2;
			switch( n )
			{
			case T00:
				break;
			case T01:
				bits |= 1;
				break;
			case T10:
				bits |= 2;
				break;
			case T11:
				bits |= 3;
				break;
			default:
				*dst++ = antic.color_lookup[n];
				*dst++ = antic.color_lookup[n];
				continue;
	        }
			switch( (bits >> 1) & 7 )
			{
			case 0: /* 0 0 0 */
			case 1: /* 0 0 1 */
			case 4: /* 1 0 0 */
				*dst++ = _D;
				break;
			case 3: /* 0 1 1 */
			case 6: /* 1 1 0 */
			case 7: /* 1 1 1 */
				*dst++ = _C;
				break;
			case 2: /* 0 1 0 */
				*dst++ = _A;
				break;
			case 5: /* 1 0 1 */
				*dst++ = _B;
				break;
			}
			switch( bits & 7 )
			{
			case 0:/* 0 0 0 */
			case 1:/* 0 0 1 */
			case 4:/* 1 0 0 */
				*dst++ = _D;
				break;
			case 3: /* 0 1 1 */
			case 6: /* 1 1 0 */
			case 7: /* 1 1 1 */
				*dst++ = _C;
				break;
			case 2: /* 0 1 0 */
				*dst++ = _B;
				break;
			case 5: /* 1 0 1 */
				*dst++ = _A;
				break;
	        }
	    }
	}
	
	
	static void antic_linerefresh(void)
	{
		int x, y;
		UINT8 *src;
		UINT32 *dst;
	
		/* increment the scanline */
	    if( ++antic.scanline == Machine.drv.screen_height )
	    {
	        /* and return to the top if the frame was complete */
	        antic.scanline = 0;
	        antic.modelines = 0;
	        /* count frames gone since last write to hitclr */
	        gtia.h.hitclr_frames++;
	    }
	
		if( antic.scanline < MIN_Y || antic.scanline > MAX_Y )
	        return;
	
		y = antic.scanline - MIN_Y;
		src = &antic.cclock[PMOFFSET - antic.hscrol_old + 12];
		dst = (UINT32 *)&Machine.scrbitmap.line[y][12];
	
		if (tv_artifacts != 0)
		{
			if( (antic.cmd & 0x0f) == 2 || (antic.cmd & 0x0f) == 3 )
			{
				artifacts_txt(src, (UINT8*)(dst + 3), HCHARS);
				return;
			}
			else
			if( (antic.cmd & 0x0f) == 15 )
			{
				artifacts_gfx(src, (UINT8*)(dst + 3), HCHARS);
				return;
			}
		}
		dst[0] = antic.color_lookup[PBK] | antic.color_lookup[PBK] << 16;
		dst[1] = antic.color_lookup[PBK] | antic.color_lookup[PBK] << 16;
		dst[2] = antic.color_lookup[PBK] | antic.color_lookup[PBK] << 16;
		dst[3] = antic.color_lookup[src[BYTE_XOR(0)]] | antic.color_lookup[src[BYTE_XOR(1)]] << 16;
	    src += 2;
		dst += 4;
		for( x = 1; x < HCHARS-1; x++ )
		{
			*dst++ = antic.color_lookup[src[BYTE_XOR(0)]] | antic.color_lookup[src[BYTE_XOR(1)]] << 16;
			*dst++ = antic.color_lookup[src[BYTE_XOR(2)]] | antic.color_lookup[src[BYTE_XOR(3)]] << 16;
			src += 4;
	    }
		dst[0] = antic.color_lookup[src[BYTE_XOR(0)]] | antic.color_lookup[src[BYTE_XOR(1)]] << 16;
		dst[1] = antic.color_lookup[PBK] | antic.color_lookup[PBK] << 16;
		dst[2] = antic.color_lookup[PBK] | antic.color_lookup[PBK] << 16;
		dst[3] = antic.color_lookup[PBK] | antic.color_lookup[PBK] << 16;
	}
	
	#if VERBOSE
	static int cycle(void)
	{
		return cpu_gethorzbeampos() * CYCLES_PER_LINE / Machine.drv.screen_width;
	}
	#endif
	
	static void after(int cycles, void (*function)(int), const char *funcname)
	{
	    double duration = cpu_getscanlineperiod() * cycles / CYCLES_PER_LINE;
	    (void)funcname;
		LOG((errorlog, "           after %3d (%5.1f us) %s\n", cycles, duration * 1.0e6, funcname));
		timer_set(duration, 0, function);
	}
	
	static void antic_issue_dli(int param)
	{
		if( antic.w.nmien & DLI_NMI )
		{
			LOG((errorlog, "           @cycle #%3d issue DLI\n", cycle()));
			antic.r.nmist |= DLI_NMI;
			cpu_set_nmi_line(0, PULSE_LINE);
		}
		else
		{
			LOG((errorlog, "           @cycle #%3d DLI not enabled\n", cycle()));
	    }
	}
	
	
	static  renderer_function renderer[2][19][5] = {
		/*	 no playfield	 narrow 		 normal 		 wide		  */
		{
			{antic_mode_0_xx,antic_mode_0_xx,antic_mode_0_xx,antic_mode_0_xx},
			{antic_mode_0_xx,antic_mode_0_xx,antic_mode_0_xx,antic_mode_0_xx},
			{antic_mode_0_xx,antic_mode_2_32,antic_mode_2_40,antic_mode_2_48},
			{antic_mode_0_xx,antic_mode_3_32,antic_mode_3_40,antic_mode_3_48},
			{antic_mode_0_xx,antic_mode_4_32,antic_mode_4_40,antic_mode_4_48},
			{antic_mode_0_xx,antic_mode_5_32,antic_mode_5_40,antic_mode_5_48},
			{antic_mode_0_xx,antic_mode_6_32,antic_mode_6_40,antic_mode_6_48},
			{antic_mode_0_xx,antic_mode_7_32,antic_mode_7_40,antic_mode_7_48},
			{antic_mode_0_xx,antic_mode_8_32,antic_mode_8_40,antic_mode_8_48},
			{antic_mode_0_xx,antic_mode_9_32,antic_mode_9_40,antic_mode_9_48},
			{antic_mode_0_xx,antic_mode_a_32,antic_mode_a_40,antic_mode_a_48},
			{antic_mode_0_xx,antic_mode_b_32,antic_mode_b_40,antic_mode_b_48},
			{antic_mode_0_xx,antic_mode_c_32,antic_mode_c_40,antic_mode_c_48},
			{antic_mode_0_xx,antic_mode_d_32,antic_mode_d_40,antic_mode_d_48},
			{antic_mode_0_xx,antic_mode_e_32,antic_mode_e_40,antic_mode_e_48},
			{antic_mode_0_xx,antic_mode_f_32,antic_mode_f_40,antic_mode_f_48},
			{antic_mode_0_xx, gtia_mode_1_32, gtia_mode_1_40, gtia_mode_1_48},
			{antic_mode_0_xx, gtia_mode_2_32, gtia_mode_2_40, gtia_mode_2_48},
			{antic_mode_0_xx, gtia_mode_3_32, gtia_mode_3_40, gtia_mode_3_48},
		},
		/*	 with hscrol enabled playfield width is +32 color clocks	  */
		/*	 no playfield	 narrow.normal  normal.wide	 wide.wide   */
		{
			{antic_mode_0_xx,antic_mode_0_xx,antic_mode_0_xx,antic_mode_0_xx},
			{antic_mode_0_xx,antic_mode_0_xx,antic_mode_0_xx,antic_mode_0_xx},
			{antic_mode_0_xx,antic_mode_2_40,antic_mode_2_48,antic_mode_2_48},
			{antic_mode_0_xx,antic_mode_3_40,antic_mode_3_48,antic_mode_3_48},
			{antic_mode_0_xx,antic_mode_4_40,antic_mode_4_48,antic_mode_4_48},
			{antic_mode_0_xx,antic_mode_5_40,antic_mode_5_48,antic_mode_5_48},
			{antic_mode_0_xx,antic_mode_6_40,antic_mode_6_48,antic_mode_6_48},
			{antic_mode_0_xx,antic_mode_7_40,antic_mode_7_48,antic_mode_7_48},
			{antic_mode_0_xx,antic_mode_8_40,antic_mode_8_48,antic_mode_8_48},
			{antic_mode_0_xx,antic_mode_9_40,antic_mode_9_48,antic_mode_9_48},
			{antic_mode_0_xx,antic_mode_a_40,antic_mode_a_48,antic_mode_a_48},
			{antic_mode_0_xx,antic_mode_b_40,antic_mode_b_48,antic_mode_b_48},
			{antic_mode_0_xx,antic_mode_c_40,antic_mode_c_48,antic_mode_c_48},
			{antic_mode_0_xx,antic_mode_d_40,antic_mode_d_48,antic_mode_d_48},
			{antic_mode_0_xx,antic_mode_e_40,antic_mode_e_48,antic_mode_e_48},
			{antic_mode_0_xx,antic_mode_f_40,antic_mode_f_48,antic_mode_f_48},
			{antic_mode_0_xx, gtia_mode_1_40, gtia_mode_1_48, gtia_mode_1_48},
			{antic_mode_0_xx, gtia_mode_2_40, gtia_mode_2_48, gtia_mode_2_48},
			{antic_mode_0_xx, gtia_mode_3_40, gtia_mode_3_48, gtia_mode_3_48},
		}
	};
	
	/*****************************************************************************
	 *
	 *	Antic Line Done
	 *
	 *****************************************************************************/
	void antic_line_done(int param)
	{
		if( antic.w.wsync )
	    {
			LOG((errorlog, "           @cycle #%3d release WSYNC\n", cycle()));
	        /* release the CPU if it was actually waiting for HSYNC */
	        cpu_trigger(TRIGGER_HSYNC);
	        /* and turn off the 'wait for hsync' flag */
	        antic.w.wsync = 0;
	    }
		LOG((errorlog, "           @cycle #%3d release CPU\n", cycle()));
	    /* release the CPU (held for emulating cycles stolen by ANTIC DMA) */
		cpu_trigger(TRIGGER_STEAL);
	
		/* refresh the display (translate color clocks to pixels) */
	    antic_linerefresh();
	}
	
	/*****************************************************************************
	 *
	 *	Antic Steal Cycles
	 *  This is called once per scanline by a interrupt issued in the
	 *  atari_scanline_render function. Set a new timer for the HSYNC
	 *  position and release the CPU; but hold it again immediately until
	 *  TRIGGER_HSYNC if WSYNC (D01A) was accessed
	 *
	 *****************************************************************************/
	void antic_steal_cycles(int param)
	{
		LOG((errorlog, "           @cycle #%3d steal %d cycles\n", cycle(), antic.steal_cycles));
		after(antic.steal_cycles, antic_line_done, "antic_line_done");
	    antic.steal_cycles = 0;
	    cpu_spinuntil_trigger(TRIGGER_STEAL);
	}
	
	
	/*****************************************************************************
	 *
	 *	Antic Scan Line Render
	 *	Render the scanline to the scrbitmap buffer.
	 *  Also transport player/missile data to the grafp and grafm registers
	 *  of the GTIA if enabled (DMA_PLAYER or DMA_MISSILE)
	 *
	 *****************************************************************************/
	void antic_scanline_render(int param)
	{
		VIDEO *video = antic.video[antic.scanline];
		LOG((errorlog, "           @cycle #%3d render mode $%X lines to go #%d\n", cycle(), (antic.cmd & 0x0f), antic.modelines));
	
	    (*antic_renderer)(video);
	
	    /* if player/missile graphics is enabled */
	    if( antic.scanline < 256 && (antic.w.dmactl & (DMA_PLAYER|DMA_MISSILE)) )
	    {
	        /* new player/missile graphics data for every scanline ? */
	        if( antic.w.dmactl & DMA_PM_DBLLINE )
	        {
	            /* transport missile data to GTIA ? */
	            if( antic.w.dmactl & DMA_MISSILE )
	            {
	                antic.steal_cycles += 1;
	                MWA_GTIA(0x11, RDPMGFXD(3*256));
	            }
	            /* transport player data to GTIA ? */
	            if( antic.w.dmactl & DMA_PLAYER )
	            {
	                antic.steal_cycles += 4;
	                MWA_GTIA(0x0d, RDPMGFXD(4*256));
	                MWA_GTIA(0x0e, RDPMGFXD(5*256));
	                MWA_GTIA(0x0f, RDPMGFXD(6*256));
	                MWA_GTIA(0x10, RDPMGFXD(7*256));
	            }
	        }
	        else
	        {
	            /* transport missile data to GTIA ? */
	            if( antic.w.dmactl & DMA_MISSILE )
	            {
					if( (antic.scanline & 1) == 0 ) 	 /* even line ? */
						antic.steal_cycles += 1;
	                MWA_GTIA(0x11, RDPMGFXS(3*128));
	            }
	            /* transport player data to GTIA ? */
	            if( antic.w.dmactl & DMA_PLAYER )
	            {
					if( (antic.scanline & 1) == 0 ) 	 /* even line ? */
						antic.steal_cycles += 4;
	                MWA_GTIA(0x0d, RDPMGFXS(4*128));
	                MWA_GTIA(0x0e, RDPMGFXS(5*128));
	                MWA_GTIA(0x0f, RDPMGFXS(6*128));
	                MWA_GTIA(0x10, RDPMGFXS(7*128));
	            }
	        }
	    }
	
	    gtia_render(video);
	
	    antic.steal_cycles += CYCLES_REFRESH;
		LOG((errorlog, "           run CPU for %d cycles\n", CYCLES_HSYNC - CYCLES_HSTART - antic.steal_cycles));
		after(CYCLES_HSYNC - CYCLES_HSTART - antic.steal_cycles, antic_steal_cycles, "antic_steal_cycles");
	}
	
	
	
	INLINE void LMS(int new_cmd)
	{
	    /**************************************************************
	     * If the LMS bit (load memory scan) of the current display
	     * list command is set, load the video source address from the
	     * following two bytes and split it up into video page/offset.
	     * Steal two more cycles from the CPU for fetching the address.
	     **************************************************************/
	    if ((new_cmd & ANTIC_LMS) != 0)
	    {
			int addr = RDANTIC();
	        antic.doffs = ++antic.doffs & DOFFS;
	        addr += 256 * RDANTIC();
	        antic.doffs = ++antic.doffs & DOFFS;
	        antic.vpage = addr & VPAGE;
	        antic.voffs = addr & VOFFS;
			LOG((errorlog, "           LMS $%04x\n", addr));
	        /* steal two more clock cycles from the cpu */
	        antic.steal_cycles += 2;
	    }
	}
	
	/*****************************************************************************
	 *
	 *	Antic Scan Line DMA
	 *	This is called once per scanline from Atari Interrupt
	 *	If the ANTIC DMA is active (DMA_ANTIC) and the scanline not inside
	 *	the VBL range (VBL_START - TOTAL_LINES or 0 - VBL_END)
	 *	check if all mode lines of the previous ANTIC command were done and
	 *	if so, read a new command and set up the renderer function
	 *
	 *****************************************************************************/
	static void antic_scanline_dma(int param)
	{
		LOG((errorlog, "           @cycle #%3d DMA fetch\n", cycle()));
		if (antic.scanline == VBL_END)
			antic.r.nmist &= ~VBL_NMI;
	    if( antic.w.dmactl & DMA_ANTIC )
		{
			if( antic.scanline >= VBL_END && antic.scanline < VBL_START )
			{
				if( antic.modelines <= 0 )
				{
					int h = 0, w = antic.w.dmactl & 3;
					UINT8 vscrol_subtract = 0;
					UINT8 new_cmd;
	
					new_cmd = RDANTIC();
					antic.doffs = ++antic.doffs & DOFFS;
					/* steal at one clock cycle from the CPU for fetching the command */
	                antic.steal_cycles += 1;
					LOG((errorlog, "           ANTIC CMD $%02x\n", new_cmd));
					/* command 1 .. 15 ? */
					if ((new_cmd & ANTIC_MODE) != 0)
					{
						antic.w.chbasl = 0;
						/* vertical scroll mode changed ? */
						if( (antic.cmd ^ new_cmd) & ANTIC_VSCR )
						{
							/* vertical scroll activate now ? */
							if ((new_cmd & ANTIC_VSCR) != 0)
							{
								antic.vscrol_old =
								vscrol_subtract =
								antic.w.chbasl = antic.w.vscrol;
							}
							else
							{
								vscrol_subtract = ~antic.vscrol_old;
							}
						}
						/* does this command have horizontal scroll enabled ? */
						if ((new_cmd & ANTIC_HSCR) != 0)
						{
							h = 1;
							antic.hscrol_old = antic.w.hscrol;
						}
						else
						{
							antic.hscrol_old = 0;
						}
					}
					/* Set the ANTIC mode renderer function */
					antic_renderer = renderer[h][new_cmd & ANTIC_MODE][w];
	
					switch( new_cmd & ANTIC_MODE )
					{
					case 0x00:
						/* generate 1 .. 8 empty lines */
						antic.modelines = ((new_cmd >> 4) & 7) + 1;
						/* did the last ANTIC command have vertical scroll enabled ? */
						if( antic.cmd & ANTIC_VSCR )
						{
							/* yes, generate vscrol_old additional empty lines */
							antic.modelines += antic.vscrol_old;
						}
						/* leave only bit 7 (DLI) set in ANTIC command */
						new_cmd &= ANTIC_DLI;
						break;
					case 0x01:
						/* ANTIC "jump" with DLI: issue interrupt immediately */
						if ((new_cmd & ANTIC_DLI) != 0)
						{
							/* remove the DLI bit */
							new_cmd &= ~ANTIC_DLI;
							after(CYCLES_DLI_NMI, antic_issue_dli, "antic_issue_dli");
						}
						/* load memory scan bit set ? */
						if ((new_cmd & ANTIC_LMS) != 0)
						{
							int addr = RDANTIC();
	                        antic.doffs = ++antic.doffs & DOFFS;
	                        addr += 256 * RDANTIC();
	                        antic.dpage = addr & DPAGE;
	                        antic.doffs = addr & DOFFS;
	                        /* produce empty scanlines until vblank start */
							antic.modelines = VBL_START + 1 - antic.scanline;
							if( antic.modelines < 0 )
								antic.modelines = Machine.drv.screen_height - antic.scanline;
							LOG((errorlog, "           JVB $%04x\n", antic.dpage|antic.doffs));
						}
						else
						{
							int addr = RDANTIC();
	                        antic.doffs = ++antic.doffs & DOFFS;
	                        addr += 256 * RDANTIC();
	                        antic.dpage = addr & DPAGE;
	                        antic.doffs = addr & DOFFS;
	                        /* produce a single empty scanline */
							antic.modelines = 1;
							LOG((errorlog, "           JMP $%04x\n", antic.dpage|antic.doffs));
						}
						break;
					case 0x02:
						LMS(new_cmd);
						antic.chbase = (antic.w.chbash & 0xfc) << 8;
						antic.modelines = 8 - (vscrol_subtract & 7);
						if( antic.w.chactl & 4 )	/* decrement chbasl? */
							antic.w.chbasl = antic.modelines - 1;
						break;
					case 0x03:
						LMS(new_cmd);
						antic.chbase = (antic.w.chbash & 0xfc) << 8;
						antic.modelines = 10 - (vscrol_subtract & 9);
						if( antic.w.chactl & 4 )	/* decrement chbasl? */
							antic.w.chbasl = antic.modelines - 1;
						break;
					case 0x04:
						LMS(new_cmd);
						antic.chbase = (antic.w.chbash & 0xfc) << 8;
						antic.modelines = 8 - (vscrol_subtract & 7);
						if( antic.w.chactl & 4 )	/* decrement chbasl? */
							antic.w.chbasl = antic.modelines - 1;
						break;
					case 0x05:
						LMS(new_cmd);
						antic.chbase = (antic.w.chbash & 0xfc) << 8;
						antic.modelines = 16 - (vscrol_subtract & 15);
						if( antic.w.chactl & 4 )	/* decrement chbasl? */
							antic.w.chbasl = antic.modelines - 1;
						break;
					case 0x06:
						LMS(new_cmd);
						antic.chbase = (antic.w.chbash & 0xfe) << 8;
						antic.modelines = 8 - (vscrol_subtract & 7);
						if( antic.w.chactl & 4 )	/* decrement chbasl? */
							antic.w.chbasl = antic.modelines - 1;
						break;
					case 0x07:
						LMS(new_cmd);
						antic.chbase = (antic.w.chbash & 0xfe) << 8;
						antic.modelines = 16 - (vscrol_subtract & 15);
						if( antic.w.chactl & 4 )	/* decrement chbasl? */
							antic.w.chbasl = antic.modelines - 1;
						break;
					case 0x08:
						LMS(new_cmd);
						antic.modelines = 8 - (vscrol_subtract & 7);
						break;
					case 0x09:
						LMS(new_cmd);
						antic.modelines = 4 - (vscrol_subtract & 3);
						break;
					case 0x0a:
						LMS(new_cmd);
						antic.modelines = 4 - (vscrol_subtract & 3);
						break;
					case 0x0b:
						LMS(new_cmd);
						antic.modelines = 2 - (vscrol_subtract & 1);
						break;
					case 0x0c:
						LMS(new_cmd);
						antic.modelines = 1;
	                    break;
					case 0x0d:
						LMS(new_cmd);
						antic.modelines = 2 - (vscrol_subtract & 1);
						break;
					case 0x0e:
						LMS(new_cmd);
						antic.modelines = 1;
	                    break;
					case 0x0f:
						LMS(new_cmd);
						/* bits 6+7 of the priority select register determine */
						/* if newer GTIA or plain graphics modes are used */
						switch (gtia.w.prior >> 6)
						{
							case 0: break;
							case 1: antic_renderer = renderer[h][16][w];  break;
							case 2: antic_renderer = renderer[h][17][w];  break;
							case 3: antic_renderer = renderer[h][18][w];  break;
						}
						antic.modelines = 1;
	                    break;
					}
					/* set new (current) antic command */
					antic.cmd = new_cmd;
	            }
	        }
			else
			{
				LOG((errorlog, "           out of visible range\n"));
				antic.cmd = 0x00;
				antic_renderer = antic_mode_0_xx;
	        }
		}
		else
		{
			LOG((errorlog, "           DMA is off\n"));
	        antic.cmd = 0x00;
			antic_renderer = antic_mode_0_xx;
		}
	
		antic.r.nmist &= ~DLI_NMI;
		if( antic.modelines == 1 && (antic.cmd & antic.w.nmien & DLI_NMI) )
			after(CYCLES_DLI_NMI, antic_issue_dli, "antic_issue_dli");
	
		after(CYCLES_HSTART, antic_scanline_render, "antic_scanline_render");
	}
	
	/*****************************************************************************
	 *
	 *	Atari 400 Interrupt Dispatcher
	 *	This is called once per scanline and handles:
	 *	vertical blank interrupt
	 *	ANTIC DMA to possibly access the next display list command
	 *
	 *****************************************************************************/
	public static InterruptPtr a400_interrupt = new InterruptPtr() { public int handler() 
	{
		LOG((errorlog, "ANTIC #%3d @cycle #%d scanline interrupt\n", antic.scanline, cycle()));
	
	    if( antic.scanline < VBL_START )
	    {
			antic_scanline_dma(0);
	        return ignore_interrupt();
	    }
	
	    if( antic.scanline == VBL_START )
	    {
			UINT8 port3 = input_port_3_r(0);
			if( (gtia.w.gractl & GTIA_TRIGGER) == 0 )
	            gtia.r.but0 = gtia.r.but1 = gtia.r.but2 = gtia.r.but3 = 1;
			gtia.r.but0 &= (port3 >> 0) & 1;
			gtia.r.but1 &= (port3 >> 1) & 1;
			gtia.r.but2 &= (port3 >> 2) & 1;
			gtia.r.but3 &= (port3 >> 3) & 1;
	
			a800_handle_keyboard();
	
	        /* do nothing new for the rest of the frame */
			antic.modelines = Machine.drv.screen_height - VBL_START;
	        antic_renderer = antic_mode_0_xx;
	
	        /* if the CPU want's to be interrupted at vertical blank... */
			if( antic.w.nmien & VBL_NMI )
	        {
				LOG((errorlog, "           cause VBL NMI\n"));
	            /* set the VBL NMI status bit */
	            antic.r.nmist |= VBL_NMI;
				cpu_set_nmi_line(0, PULSE_LINE);
	        }
	    }
		/* refresh the display (translate color clocks to pixels) */
	    antic_linerefresh();
	    return ignore_interrupt();
	} };
	
	/*****************************************************************************
	 *
	 *	Atari 800 Interrupt Dispatcher
	 *	This is called once per scanline and handles:
	 *	vertical blank interrupt
	 *	ANTIC DMA to possibly access the next display list command
	 *
	 *****************************************************************************/
	public static InterruptPtr a800_interrupt = new InterruptPtr() { public int handler() 
	{
		LOG((errorlog, "ANTIC #%3d @cycle #%d scanline interrupt\n", antic.scanline, cycle()));
	
	    if( antic.scanline < VBL_START )
	    {
			antic_scanline_dma(0);
	        return ignore_interrupt();
	    }
	
	    if( antic.scanline == VBL_START )
	    {
			UINT8 port3 = input_port_3_r(0);
			if( (gtia.w.gractl & GTIA_TRIGGER) == 0 )
	            gtia.r.but0 = gtia.r.but1 = gtia.r.but2 = gtia.r.but3 = 1;
			gtia.r.but0 &= (port3 >> 0) & 1;
			gtia.r.but1 &= (port3 >> 1) & 1;
			gtia.r.but2 &= (port3 >> 2) & 1;
			gtia.r.but3 &= (port3 >> 3) & 1;
	
			a800_handle_keyboard();
	
	        /* do nothing new for the rest of the frame */
			antic.modelines = Machine.drv.screen_height - VBL_START;
	        antic_renderer = antic_mode_0_xx;
	
	        /* if the CPU want's to be interrupted at vertical blank... */
			if( antic.w.nmien & VBL_NMI )
	        {
				LOG((errorlog, "           cause VBL NMI\n"));
	            /* set the VBL NMI status bit */
	            antic.r.nmist |= VBL_NMI;
				cpu_set_nmi_line(0, PULSE_LINE);
	        }
	    }
		/* refresh the display (translate color clocks to pixels) */
	    antic_linerefresh();
	    return ignore_interrupt();
	} };
	
	/*****************************************************************************
	 *
	 *	Atari 800XL Interrupt Dispatcher
	 *	This is called once per scanline and handles:
	 *	vertical blank interrupt
	 *	ANTIC DMA to possibly access the next display list command
	 *
	 *****************************************************************************/
	public static InterruptPtr a800xl_interrupt = new InterruptPtr() { public int handler() 
	{
		LOG((errorlog, "ANTIC #%3d @cycle #%d scanline interrupt\n", antic.scanline, cycle()));
	
	    if( antic.scanline < VBL_START )
	    {
			antic_scanline_dma(0);
	        return ignore_interrupt();
	    }
	
	    if( antic.scanline == VBL_START )
	    {
			UINT8 port3 = input_port_3_r(0);
			if( (gtia.w.gractl & GTIA_TRIGGER) == 0 )
				gtia.r.but0 = gtia.r.but1 = 1;
			gtia.r.but0 &= (port3 >> 0) & 1;
			gtia.r.but1 &= (port3 >> 1) & 1;
			gtia.r.but2 = 1;
			gtia.r.but3 = 1;
	
			a800_handle_keyboard();
	
	        /* do nothing new for the rest of the frame */
			antic.modelines = Machine.drv.screen_height - VBL_START;
	        antic_renderer = antic_mode_0_xx;
	
	        /* if the CPU want's to be interrupted at vertical blank... */
			if( antic.w.nmien & VBL_NMI )
	        {
				LOG((errorlog, "           cause VBL NMI\n"));
	            /* set the VBL NMI status bit */
	            antic.r.nmist |= VBL_NMI;
				cpu_set_nmi_line(0, PULSE_LINE);
	        }
	    }
		/* refresh the display (translate color clocks to pixels) */
	    antic_linerefresh();
	    return ignore_interrupt();
	} };
	
	/*****************************************************************************
	 *
	 *	VCS 5200 Interrupt Dispatcher
	 *	This is called once per scanline and handles:
	 *	vertical blank interrupt
	 *	ANTIC DMA to possibly access the next display list command
	 *
	 *****************************************************************************/
	public static InterruptPtr a5200_interrupt = new InterruptPtr() { public int handler() 
	{
		LOG((errorlog, "ANTIC #%3d @cycle #%d scanline interrupt\n", antic.scanline, cycle()));
	
	    if( antic.scanline < VBL_START )
	    {
			antic_scanline_dma(0);
	        return ignore_interrupt();
	    }
	
	    if( antic.scanline == VBL_START )
	    {
			UINT8 port3 = input_port_3_r(0);
	        if( (gtia.w.gractl & GTIA_TRIGGER) == 0 )
	            gtia.r.but0 = gtia.r.but1 = gtia.r.but2 = gtia.r.but3 = 1;
			gtia.r.but0 &= (port3 >> 0) & 1;
			gtia.r.but1 &= (port3 >> 1) & 1;
			gtia.r.but2 &= (port3 >> 2) & 1;
	        gtia.r.but3 &= (port3 >> 3) & 1;
	
			a5200_handle_keypads();
	
	        /* do nothing new for the rest of the frame */
			antic.modelines = Machine.drv.screen_height - VBL_START;
	        antic_renderer = antic_mode_0_xx;
	
	        /* if the CPU want's to be interrupted at vertical blank... */
			if( antic.w.nmien & VBL_NMI )
	        {
				LOG((errorlog, "           cause VBL NMI\n"));
	            /* set the VBL NMI status bit */
	            antic.r.nmist |= VBL_NMI;
				cpu_set_nmi_line(0, PULSE_LINE);
	        }
	    }
		/* refresh the display (translate color clocks to pixels) */
	    antic_linerefresh();
	    return ignore_interrupt();
	} };
	
	
}
