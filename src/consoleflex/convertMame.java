/*
This file is part of ConsoleFlex.

Arcadeflex is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Arcadeflex is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Arcadeflex.  If not, see <http://www.gnu.org/licenses/>.
 */
package consoleflex;

/**
 * @author shadow
 */
public class convertMame {

    public static void ConvertMame() {
        Analyse();
        Convert();
    }

    public static void Analyse() {

    }

    static final int GAMEDRIVER = 0;
    static final int Samplesinterface = 1;
    static final int READHANDLER = 2;
    static final int WRITEHANDLER = 3;
    static final int MACHINE_INTERRUPT = 4;
    static final int DRIVER_INIT = 5;
    static final int MACHINE_INIT = 6;
    static final int VH_STOP = 7;
    static final int VH_START = 8;
    static final int VH_SCREENREFRESH = 9;
    static final int GFXLAYOUT = 10;
    static final int GFXDECODE = 11;
    static final int VLM5030interface = 12;
    static final int VH_CONVERT = 13;
    static final int SH_UPDATE = 14;
    static final int SH_START = 15;
    static final int SH_STOP = 16;
    static final int NESinterface = 17;
    static final int SN76496interface = 18;
    static final int DACinterface = 19;
    static final int PLOT_BOX = 20;
    static final int MARK_DIRTY = 21;
    static final int PLOT_PIXEL = 22;
    static final int VH_EOF = 23;
    static final int NVRAM_H = 24;
    static final int MACHINEDRIVER = 25;
    static final int AY8910interface = 26;
    static final int YM3812interface = 27;
    static final int YM3526interface = 28;
    static final int MSM5205interface = 29;
    static final int YM2203interface = 30;
    static final int K054539interface = 31;
    static final int EEPROM_interface = 32;
    static final int MEMORYREAD = 33;
    static final int MEMORYWRITE =34;
    static final int IOREAD = 35;
    static final int IOWRITE = 36;
    static final int OKIM6295interface = 37;
    static final int YM2413interface=38;
    static final int POKEYinterface=39;
    static final int YM2151interface=40;
    static final int UPD7759_interface=41;
    static final int CustomSound_interface=42;
    static final int YM2610interface=43;
    static final int TIMERCALLBACK=44;
    static final int C140interface=45;
    static final int astrocade_interface=46;
    static final int RF5C68interface=47;
    static final int k051649_interface=48;
    static final int hc55516_interface=49;
    static final int vclk_interruptPtr=50;
    static final int konami_cpu_setlines_callbackPtr=51;
    static final int namco_interface=52;
    static final int SN76477interface=53;
    static final int K052109=54;
    static final int K051960=55;
    static final int TILEINFO=56;

    //type2 fields
    static final int NEWINPUT = 130;
    static final int ROMDEF = 131;

    public static void Convert() {
        Convertor.inpos = 0;//position of pointer inside the buffers
        Convertor.outpos = 0;
        boolean only_once_flag = false;//gia na baleis to header mono mia fora
        boolean line_change_flag = false;

        int kapa = 0;
        int i = 0;
        int type = 0;
        int i3 = -1;
        int i8 = -1;
        int type2 = 0;
        int[] insideagk = new int[10];//get the { that are inside functions

        do {
            if (Convertor.inpos >= Convertor.inbuf.length)//an to megethos einai megalitero spase to loop
            {
                break;
            }
            char c = sUtil.getChar(); //pare ton character
            if (line_change_flag) {
                for (int i1 = 0; i1 < kapa; i1++) {
                    sUtil.putString("\t");
                }

                line_change_flag = false;
            }
            switch (c) {
                case 35: // '#'
                {
                    if (!sUtil.getToken("#include"))//an den einai #include min to trexeis
                    {
                        break;
                    }
                    sUtil.skipLine();
                    if (!only_once_flag)//trekse auto to komati mono otan bris to proto include
                    {
                        only_once_flag = true;
                        sUtil.putString("/*\r\n");
                        sUtil.putString(" * ported to v" + Convertor.mameversion + "\r\n");
                        sUtil.putString(" * using automatic conversion tool v" + Convertor.convertorversion + "\r\n");
                        /*sUtil.putString(" * converted at : " + Convertor.timenow() + "\r\n");*/
                        sUtil.putString(" */ \r\n");
                        sUtil.putString("package " + Convertor.packageName + ";\r\n");
                        sUtil.putString("\r\n");
                        sUtil.putString((new StringBuilder()).append("public class ").append(Convertor.className).append("\r\n").toString());
                        sUtil.putString("{\r\n");
                        kapa = 1;
                        line_change_flag = true;
                    }
                    continue;
                }
                case 10: // '\n'
                {
                    Convertor.outbuf[Convertor.outpos++] = Convertor.inbuf[Convertor.inpos++];
                    line_change_flag = true;
                    continue;
                }
                
                //break;
            }
            Convertor.outbuf[Convertor.outpos++] = Convertor.inbuf[Convertor.inpos++];//grapse to inputbuffer sto output
        } while (true);
        if (only_once_flag) {
            sUtil.putString("}\r\n");
        }
    }
}
