GOscTimer : StaticText {
    var labelFont;
    var osc_client, osc_port;

    *new {
        arg parent, bounds = Rect(0, 0, 240, 50), oscPort = 10001;
        ^super.new(parent, bounds).init(oscPort);
    }

    init { |port|
        labelFont = Font("Helvetica", 40);
        this.font_(labelFont);
        // this.stringColor_(Color.blue);
        this.string_("00:00:00");

        osc_port = port;
        osc_client = OSCFunc({ |msg|
            {
                // msg[1].postln;
                this.string_(msg[1]);
            }.defer;
        },
        '/gOscTimer', nil, osc_port);

        this.onClose = {
            osc_client.free;
        }
    }
}