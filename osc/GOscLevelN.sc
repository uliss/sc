GOscLevelN : GLevelN {
    var osc_client, osc_port;

    *new {
        arg parent, bounds = Rect(0, 0, 102, 160), number = 5, oscPort = 10001;
        ^super.new(parent, bounds, number: number).init(number, oscPort);
    }

    init2 {
        arg num, port;
        super.init(num);

        osc_port = port;
        osc_client = OSCFunc({ |msg| {msg.postln;}.defer }, '/playbuf_levels');
/*        OSCFunc({
            arg msg;
            {
                msg.postln;
                /*num.do {|i|
                    msg[i].postln;
                    this.value(msg[i]);
                    this.peakLevel(msg[i]);
                }*/
            }.defer;
        }, '/gOscLevelN');*/
    }
}