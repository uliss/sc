GLevel4 : CompositeView {
    var levels;

    *new {
        arg parent, bounds = Rect(0, 0, 102, 160), number = 5;
        ^super.new(parent, bounds).init(number);
    }

    init {
        arg num;
        levels = Array.new(32);

        num.do( {
            arg i;
            levels.add(GLevel.new(this, title: "ch" ++ i));
            levels[i].moveTo(i * 25, 0);
        });

        super.resizeTo(num * 25, 160);
    }

    title {
        arg idx, title;
        levels[idx].title(title);
    }

    item {
        arg idx;
        ^levels[idx];
    }
}