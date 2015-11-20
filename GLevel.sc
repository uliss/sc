GLevel : CompositeView {
    var <level, txt;

    *new {
        arg parent, bounds = Rect(0, 0, 21, 140), title = "";
        ^super.new(parent, bounds).init(title);
    }

    init {
        arg title;
        level = GLevelInd.new(this);
        txt = StaticText.new(this, Rect(0, 120, 20, 20)).font_(Font("Helvetica", 9)).string_(title);
    }

    title {
        arg title;
        txt.string_(title);
    }
}