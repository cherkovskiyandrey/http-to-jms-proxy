
function Application() {
    this._navCurrentState = $("#nav_current_state");
    this._navHistoryData = $("#nav_history_data");
    this._currentStatePage = new CurrentStatePage();
    this._historyDataPage = new HistoryDataPage();
}

Application.prototype.start = function() {
    this._currentStatePage.start();
    this._navCurrentState.click(this._chooseCurrentState.bind(this));
    this._navHistoryData.click(this._chooseHistoryData.bind(this));
    this._chooseCurrentState();
}

Application.prototype._chooseCurrentState = function() {
    if(this._navHistoryData.hasClass("active")) {
        this._navHistoryData.removeClass("active")
    }
    this._historyDataPage.stopAndHidden();

    if(!this._navCurrentState.hasClass("active")) {
        this._navCurrentState.addClass("active")
    }
    //this._currentStatePage.startAndShow();
    this._currentStatePage.show();
}

Application.prototype._chooseHistoryData = function() {
    if(this._navCurrentState.hasClass("active")) {
        this._navCurrentState.removeClass("active")
    }
    //this._currentStatePage.stopAndHidden();
    this._currentStatePage.hidden();

    if(!this._navHistoryData.hasClass("active")) {
        this._navHistoryData.addClass("active")
    }
    this._historyDataPage.startAndShow();
}

$(
    function() {
        var application = new Application();
        application.start();
    }
);
