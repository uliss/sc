SP_Midi {
    *trace {
        |sec = 10|
        MIDIIn.trace(true);
        {MIDIIn.trace(false)}.defer(sec);
    }
}