
function DataLoader(loaderWidget, errorReportWidget, urlHistory, timeout) {
    this._loaderWidget = $("#" + loaderWidget);
    this._errorReportWidget = $("#" + errorReportWidget);
    this._urlHistory = urlHistory;
    this._handlers = [];
    this._timeout = timeout;
}

DataLoader.prototype.subscribe = function(handler) {
    this._handlers.push(handler);
}

DataLoader.prototype.unsubscribe = function(handler) {
    var index = this._handlers.indexOf(handler);
    if(index != -1) {
        this._handlers.splice(index, 1);
    }
}

DataLoader.prototype._validate = function(filterObject) {
    if(filterObject.from === undefined ||
        filterObject.from === null ||
        !moment(filterObject.from).isValid()) {
        throw new Error("Дата и время начала не определены");
    }

    if(filterObject.from === undefined ||
        filterObject.from === null ||
        !moment(filterObject.from).isValid()) {
        throw new Error("Дата и время конца не определены");
    }

    if(!moment(filterObject.from).isBefore(filterObject.to)) {
        throw new Error("Невалидный временной диапазон");
    }

    if(filterObject.pageNumber === undefined ||
        filterObject.pageNumber === null ||
        (typeof filterObject.pageNumber != 'number') ||
        filterObject.pageNumber < 0) {
        throw new Error("Неверный номер страницы");
    }

    if(filterObject.pageSize === undefined ||
        filterObject.pageSize === null ||
        (typeof filterObject.pageSize != 'number') ||
        filterObject.pageSize < 0) {
        throw new Error("Неверный размер страницы");
    }
}

DataLoader.prototype.loadData = function(filterObject) {
    try {
        this._validate(filterObject);
    } catch(err) {
        this._onErrorLoading(null, err.name, err.message);
        return;
    }
    this._beginLoadingWidget();
    var filterObjectAsJson = JSON.stringify(filterObject);
    $.ajax({
        url: this._urlHistory,
        method: "POST",
        timeout: this._timeout,
        data: filterObjectAsJson,
        dataType: "json",
        error: this._onErrorLoading.bind(this),
        success: this._onOkLoading.bind(this)
    });
}

DataLoader.prototype._beginLoadingWidget = function() {
    if(this._loaderWidget.hasClass("loader_off")) {
        this._loaderWidget.removeClass("loader_off");
    }
    var loaderWidget = this._loaderWidget;
    $(window).on("scroll.DataLoader", function(e) {
        var bodyHeight = $('body').outerHeight();
        var scrolled = $(window).scrollTop();
        loaderWidget.css('height', (bodyHeight - scrolled) + 'px');
    });
}

DataLoader.prototype._endLoadingWidget = function() {
    if(!this._loaderWidget.hasClass("loader_off")) {
        this._loaderWidget.addClass("loader_off");
    }
    $(window).off("scroll.DataLoader");
}

DataLoader.prototype.loadByUUID = function(url, uuid, onSucc) {
    this._beginLoadingWidget();
    $.ajax({
        url: url + (!url.endsWith("/") ? "/" : "") + uuid,
        method: "GET",
        timeout: this._timeout,
        error: this._onErrorLoading.bind(this),
        success: this._onLoadSuccWrapper.bind(this, onSucc)
    });
}

DataLoader.prototype._onLoadSuccWrapper = function(onSucc, data) {
    this._endLoadingWidget();
    onSucc(data);
}

DataLoader.prototype._onOkLoading = function(data, textStatus, jqXHR) {
    this._endLoadingWidget();
    for(var i = 0; i < this._handlers.length; ++i) {
        try {
            this._handlers[i].update(data);
        } catch(err){
            console.log(err);
        }
    }
}

DataLoader.prototype._onErrorLoading = function(jqXHR, textStatus, errorThrown) {
    this._endLoadingWidget();

    var alertSubDiv = $("<div class=\"alert alert-danger alert-dismissable\"></div>");
    var alertClosedRef = $("<a href=\"#\" class=\"close\" data-dismiss=\"alert\" aria-label=\"close\">&times;</a>");
    var textError = $("<strong>" + textStatus +
        " " + errorThrown.toString() +
        " " + (jqXHR !== null ? jqXHR.responseText : "") +
        "</strong>");

    alertClosedRef.appendTo(alertSubDiv);
    textError.appendTo(alertSubDiv);
    alertSubDiv.appendTo(this._errorReportWidget);
}