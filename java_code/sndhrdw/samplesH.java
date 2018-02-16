//
// /home/ms/source/sidplay/libsidplay/emu/RCS/samples.h,v
//

#ifndef __SAMPLES_H_
#define __SAMPLES_H_


/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */ 
package sndhrdw;

public class samplesH
{
	//
	
	extern void sampleEmuCheckForInit(void);
	extern void sampleEmuInit(void);          // precalculate tables + reset
	extern void sampleEmuReset(void);         // reset some important variables
	
	extern sbyte (*sampleEmuRout)(void);
	
	
	#endif
}
