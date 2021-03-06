
/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */ 
package includes;

public class vgaH
{
	
	
	/* 
	   include in memory read list
	   { 0xa0000, 0xaffff, MRA_BANK1 }
	   { 0xb0000, 0xb7fff, MRA_BANK2 }
	   { 0xb8000, 0xbffff, MRA_BANK3 }
	   { 0xc0000, 0xc7fff, MRA_ROM }
	
	   and in memory write list
	   { 0xa0000, 0xaffff, MWA_BANK1 }
	   { 0xb0000, 0xb7fff, MWA_BANK2 }
	   { 0xb8000, 0xbffff, MWA_BANK3 }
	   { 0xc0000, 0xc7fff, MWA_ROM }
	*/
	
	extern unsigned char vga_palette[0x100*3];
	extern unsigned char ega_palette[0x40*3];
	
	void vga_init(mem_read_handler read_dipswitch);
	
	
	// include in port access list
	
	
	
	
	
	
	
	/*
	  pega notes (paradise)
	  build in amstrad pc1640
	
	  ROM_LOAD("40100", 0xc0000, 0x8000, 0xd2d1f1ae);
	
	  4 additional dipswitches
	  seems to have emulation modes at register level
	  (mda/hgc lines bit 8 not identical to ega/vga)
	
	  standard ega/vga dipswitches
	  00000000	320x200
	  00000001	640x200 hanging
	  00000010	640x200 hanging
	  00000011	640x200 hanging
	
	  00000100	640x350 hanging
	  00000101	640x350 hanging EGA mono
	  00000110	320x200
	  00000111	640x200
	
	  00001000	640x200
	  00001001	640x200
	  00001010	720x350 partial visible
	  00001011	720x350 partial visible
	
	  00001100	320x200
	  00001101	320x200
	  00001110	320x200
	  00001111	320x200
	
	*/
	
	/*
	  oak vga (oti 037 chip)
	  (below bios patch needed for running)
	
	  ROM_LOAD("oakvga.bin", 0xc0000, 0x8000, 0x318c5f43);
	*/
	#if 0
	        int i; 
	        UINT8 *memory=memory_region(REGION_CPU1)+0xc0000;
	        UINT8 chksum;
	
			/* oak vga */
	        /* plausibility check of retrace signals goes wrong */
	        memory[0x00f5]=memory[0x00f6]=memory[0x00f7]=0x90;
	        memory[0x00f8]=memory[0x00f9]=memory[0x00fa]=0x90;
	        for (chksum=0, i=0;i<0x7fff;i++) {
	                chksum+=memory[i];
	        }
	        memory[i]=0x100-chksum;
	#endif
}
