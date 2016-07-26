SP_Wav2Png {
    var path;
    var out;
    var <width;
    var <height;
    var fg;
    var bg;

    *new {
        arg path, out = nil, w = 1000, h = 150, fg = "2e4562ff", bg = "00000000";
        ^super.new.init(path, out, w, h);
    }

    init {
        arg path_, out_, w, h, fg_color, bg_color;
        path = path_;
        out = out_;
        width = w;
        height = h;
        fg = fg_color;
        bg = bg_color;

        if(path_.pathExists == false) {
            "File not exists: %".format(path_.quote).error;
            ^nil;
        };
    }

    convert {
        var cmd = "~/bin/wav2png".standardizePath;
        cmd = cmd + "-w" + width;
        cmd = cmd + "-h" + height;
        cmd = cmd + "-f" + "2e4562ff" + "-b" + "00000000";
        if(out.notNil) {
            cmd = cmd + "-o" + out.escapeChar($").quote;
        };
        cmd = cmd + path.escapeChar($").quote;
        cmd.systemCmd;
    }
}