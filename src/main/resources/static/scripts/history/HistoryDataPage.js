
function HistoryDataPage() {
    this._dataLoader = null;
    this._filter = null;
    this._pagination = null;
    this._grid = null;
    this._container = $("#history-data");
    this.stopAndHidden();
}

HistoryDataPage.prototype.start = function() {
    this._initElements();
}

HistoryDataPage.prototype.show = function() {
    this._showForm();
}

HistoryDataPage.prototype.startAndShow = function() {
    this.start();
    this.show();
}

HistoryDataPage.prototype.hidden = function() {
    this._hiddenForm();
}

HistoryDataPage.prototype.stopAndHidden = function() {
    this._hiddenForm();
    this._destroyElements();
}


HistoryDataPage.prototype._initElements = function() {
    this._dataLoader = new DataLoader("loader", "alert", "/history", 10000);
    this._filter = new FormFilter("filter_form", this._dataLoader, 10);
    this._pagination = new Pagination("pagination", this._dataLoader, this._filter);
    this._grid = new Grid("grid", "body_modal", "error_modal", "/history/body", "/history/error", this._dataLoader);
}

HistoryDataPage.prototype._destroyElements = function() {
    if(this._grid != null) {
        this._grid.destroy();
        this._grid = null;
    }
    if(this._pagination != null) {
        this._pagination.destroy();
    }
    if(this._filter != null) {
        this._filter.destroy();
    }
    this._dataLoader = null;
}

HistoryDataPage.prototype._showForm = function() {
    this._container.show();
}

HistoryDataPage.prototype._hiddenForm = function() {
    this._container.hide();
}