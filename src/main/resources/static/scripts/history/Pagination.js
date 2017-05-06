
function Pagination(id, dataLoader, mainFilter) {
    this._id = id;
    this._pageNumber = 1;
    this._maxPages = 1;
    this._mainList = $("#" + this._id);
    this._dataLoader = dataLoader;
    this._mainFilter = mainFilter;
    this._dataLoader.subscribe(this);
}

Pagination.prototype._PREV = function(){};
Pagination.prototype._NEXT = function(){};

Pagination.prototype.update = function(historyData) {
    if(historyData.pageNumber > historyData.maxPages) {
        throw new RangeError("Page number must be less than max page number: [pageNumber: " +
            historyData.pageNumber + "; maxPages: " + historyData.maxPages + "]");
    }
    this._pageNumber = historyData.pageNumber;
    this._maxPage = historyData.maxPage;
    this._redraw();
}

Pagination.prototype.destroy = function() {
   this._mainList.empty();
   this._dataLoader.unsubscribe(this);
}

Pagination.prototype._beginOfGroupFor = function(num) {
    return Math.floor((num - 1)/10)*10 + 1;
}

Pagination.prototype._endOfGroupFor = function(num, max) {
    return Math.min(Math.ceil(num/10)*10, max);
}

Pagination.prototype._redraw = function() {
    this._mainList.empty();

    var beginOfGroup = this._beginOfGroupFor(this._pageNumber);
    var endOfGroup = this._endOfGroupFor(this._pageNumber, this._maxPage);

    if(this._maxPage <= 1) {
        return;
    }

    if(beginOfGroup != 1) {
        this._addFirstElement();
    }
    for(var i = beginOfGroup; i <= endOfGroup; ++i) {
        var newElement = $("<li><a href=\"#\">" + i + "</a></li>");
        newElement.click(this._onChoosePage.bind(this, i));
        if(i == this._pageNumber) {
            newElement.addClass("active");
        }
        newElement.appendTo(this._mainList);
    }
    if(this._beginOfGroupFor(this._pageNumber) != this._beginOfGroupFor(this._maxPage)) {
        this._addLastElement();
    }
}

Pagination.prototype._addFirstElement = function() {
    var newElement = $("<li><a href=\"#\">&#171;</a></li>");
    newElement.click(this._onChoosePage.bind(this, this._PREV));
    newElement.appendTo(this._mainList);
}

Pagination.prototype._addLastElement = function() {
    var newElement = $("<li><a href=\"#\">&#187;</a></li>");
    newElement.click(this._onChoosePage.bind(this, this._NEXT));
    newElement.appendTo(this._mainList);
}


Pagination.prototype._onChoosePage = function(idOfElement, event) {
    event.preventDefault();
    if(this._pageNumber === idOfElement) {
        return;
    }

    var curFilterObject = this._mainFilter.getCurrentFilterObject();
    if(idOfElement === this._PREV) {
        curFilterObject.pageNumber = this._pageNumber - 10
        this._dataLoader.loadData(curFilterObject);
        return;
    }

    if(idOfElement === this._NEXT) {
        curFilterObject.pageNumber = this._pageNumber + 10
        this._dataLoader.loadData(curFilterObject);
        return;
    }

    curFilterObject.pageNumber = idOfElement
    this._dataLoader.loadData(curFilterObject);
}