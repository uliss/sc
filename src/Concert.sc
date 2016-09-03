SP_SheetMusicPiece : GuidoPieceApp {
    classvar <>gsPath;
    var slideshow;
    var <>namedTaskActions;
    var turns_tmp_file;

    *new {
        arg title, composer, oscPath, params = [];
        var instance = super.new(title, composer, oscPath, params);
        if(instance.notNil) {
            instance.initSheetMusic.initPageTurns;
        };
        ^instance;
    }

    *initClass {
        gsPath = "/usr/local/bin/gs"
    }

    addNamedTaskAction {
        arg name, func;
        if(namedTaskActions.isNil) { namedTaskActions = Dictionary.new };
        namedTaskActions[name] = func;
    }

    startTurnsRecord {
        var path = this.class.filenameSymbol.asString.dirname +/+ "turns.txt";
        "[%] RECORDING page turns to file: %".format(this.class, path.quote).postln;

        turns_tmp_file = File.new(path, "w");
        slideshow.onTurn = { |page|
            var time_at = currentTime.asTimeString.drop(-4).drop(3);
            "TURN happens at (%) to page: %".format(time_at, page).postln;
            turns_tmp_file.write("# turn to page:" + page.asString ++ "\n");
            turns_tmp_file.write(time_at ++ "\n");
            turns_tmp_file.flush;
        };
    }

    stopTurnsRecord {
        turns_tmp_file.close;
        slideshow.onTurn = nil;
        "[%] STOP page turns record".format(this.class).postln;
    }

    initPageTurns {}

    initSheetMusic {
        slideshow = NodeJS_Slideshow.new(nil, [\hideButtons, true]).swipeDir_(-1);
        this.addWidget(\sheetMusic, slideshow);
        this.initScore;
    }

    initScore {}

    swipe_ { |v| slideshow.params[\noSwipe] = v.not }

    addPage {
        arg imagePath, forceCopy = false;
        slideshow.addImageCopy(imagePath, 1600@1600, forceCopy);
    }

    addPages {
        arg lst, forceCopy = false;
        slideshow.addImagesCopy(lst, 1600@1600, forceCopy);
    }

    addPdf {
        arg path, force = false;
        var images;

        if(path.pathExists === false) { "[%] file not exists: %".format(this.class, path).warn; ^nil };

        images = this.splitPdf(path, force: force);
        if(images.isNil) { ^nil };

        slideshow.addImagesCopy(images, 1800@1800);
    }

    schedTurnNext {
        arg time;
        this.addTask(time, { |t|
            "[%] page turn at %".format(this.class, t).postln;
            this.turnNext
        });
        "[%] adding page turn at %".format(this.class, time).postln;
    }

    schedTurnToPage {
        arg time, page;
        this.addTask(time, { |t|
            "[%] page turn to page 2 at (%)".format(this.class, page, t).postln;
            this.toPage(page);
        });
        "[%] adding page turn to page % at (%)".format(this.class, page, time).postln;
    }

    loadPageTurns {
        arg path;
        var f;

        if(path.pathExists === false) { ^nil };

        f = File.new(path, "r");
        f.readAllString.split(Char.nl)
          .collect({|l| l.trim }) // trim all whitespaces
          .reject({|l| l.isEmpty })  // skip empty lines
          .reject({|l| l[0] == $# }) // skip comment
          .do { |ln|
            var time, action, argument;
            #time, action, argument = ln.split($ );
            switch(action,
                "turn", { this.schedTurnNext(time) },
                "page", { this.schedTurnToPage(time, argument.asInteger) },
                nil, { this.schedTurnNext(time) },
                {
                    var func = namedTaskActions[action.asSymbol];
                    if(func.notNil) {
                        "[%] adding action % at (%)".format(this.class, action.quote, time).postln;
                        this.addTask(time, { func.value(time, argument) });

                    } {
                        "[%] unknown action % at (%)".format(this.class, action.quote, time).warn
                    }
                };
            );
        };
        f.close;
    }

    uid {
        ^(composer + title).hash;
    }

    splitPdf {
        arg path, resolution = 400, force = false;
        var dir = PathName.tmp;
        var out_template = dir +/+ "page_%_%03d.png".format(this.uid, $%);
        var out_pattern = dir +/+ "page_%_*.png".format(this.uid);

        if(out_pattern.pathMatch.isEmpty || force) {
            var err = 0;
            var cmd = gsPath + "-sDEVICE=pnggray" + "-q" + "-dBATCH" + "-dNOPAUSE" + "-r%".format(resolution) + "-sOutputFile=%".format(out_template.quote) + path.quote;
            cmd.postln;
            err = cmd.systemCmd;
            if(err != 0) { ^nil };
            ^out_pattern.pathMatch;
        };

        ^out_pattern.pathMatch;
    }

    syncTitle {
        NodeJS.sendMsg("/node/title", "");
    }

    turnPrev { slideshow.prev }
    turnNext { slideshow.next }
    turnLast { slideshow.last }
    turnFirst { slideshow.first }
    toPage { |n| slideshow.toImage(n) }

    *turnsDir {
        ^this.filenameSymbol.asString.dirname +/+ "turns";
    }

    *scoresDir {
        ^this.filenameSymbol.asString.dirname +/+ "scores";
    }
}

SP_PdfMusicPiece : SP_SheetMusicPiece {
    *new {
        arg pdf, title, composer = "PDF", oscPath = "/sheetmusic", params = [];
        var instance;
        title = title ? pdf.basename;

        instance = super.new(title, composer, oscPath, params);
        if(instance.notNil) { instance.addPdf(pdf) };
        ^instance;
    }
}
