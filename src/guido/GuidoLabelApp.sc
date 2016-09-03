GuidoLabelApp : GuidoAbstractApp {
    var <color;
    var <backgroundColor;
    var <text;

    *new {
        arg txt = "DEFAULT", autoSync = true;
        ^super.new("/vlabel", "/vlabel", autoSync).text_(txt).color_("black").backgroundColor_("transparent")
    }

    text_ {
        arg txt;
        text = txt.urlEncode;
        this.sendMsg("/set", text);
    }

    sync {
        this.sendMsg("/set", text);
    }

    color_ {
        arg c = "#000000";
        color = c;
        this.css("color", color);
    }

    backgroundColor_ {
        arg c = "#FFFFFF";
        if(c.isNil) { c = "transparent" };
        backgroundColor = c;
        this.css("background-color", backgroundColor);
    }

    blink {
        arg ms = 100, c = "#FF0000";
        var prev_color = backgroundColor;

        this.backgroundColor_(c);

        {
            this.backgroundColor_(prev_color)
        }.defer(ms / 1000);
    }

    invert {
        var bgcolor = backgroundColor;
        this.backgroundColor_(color);
        this.color_(bgcolor);
    }
}
