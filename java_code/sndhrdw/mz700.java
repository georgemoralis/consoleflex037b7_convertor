/***************************************************************************
 *	Sharp MZ700
 *
 *	sound hardware
 *
 *	Juergen Buchmueller <pullmoll@t-online.de>, Jul 2000
 *
 *  Reference: http://sharpmz.computingmuseum.com
 *
 ***************************************************************************/

/*
 * ported to v0.37b7
 * using automatic conversion tool v0.01
 */ 
package sndhrdw;

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
	
	static int channel;
	static int baseclock = 1;
	
	static void mz700_sound_output(int param, INT16 *buffer, int length)
	{
		static INT16 signal = 0x7fff;
	    static int incr = 0;
		int rate = Machine.sample_rate / 2;
	
		while( length-- > 0 )
		{
			*buffer++ = signal;
			incr -= baseclock;
			while( incr < 0 )
			{
				incr += rate;
				signal = -signal;
			}
		}
	}
	
	int mz700_sh_start(const struct MachineSound* driver)
	{
		logerror("pc_sh_start\n");
		channel = stream_init("PC speaker", 50, Machine.sample_rate, 0, mz700_sound_output);
	    return 0;
	}
	
	public static ShStopPtr mz700_sh_stop = new ShStopPtr() { public void handler() 
	{
	} };
	
	public static ShUpdatePtr mz700_sh_update = new ShUpdatePtr() { public void handler() 
	{
		stream_update(channel, 0);
	} };
	
	void mz700_sh_set_clock(int clock)
	{
		stream_update(channel, 0);
	    baseclock = clock;
		LOG(1,"mz700_sh_set_clock",("%d Hz\n", clock));
	}
}
