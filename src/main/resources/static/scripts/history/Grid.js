
function Grid(gridId, modalBodyId, modalErrorId, bodyUrl, errorUrl, dataLoader) {
    this._gridId = gridId;
    this._modalBodyId = modalBodyId;
    this._modalErrorId = modalErrorId;
    this._tbody = $("#" + this._gridId + " table tbody");
    this._modalBodyDiv = $("#" + this._modalBodyId);
    this._modalBody = $("#" + this._modalBodyId + " .modal-body");
    this._modalErrorDiv = $("#" + this._modalErrorId);
    this._modalError = $("#" + this._modalErrorId + " .modal-body");
    this._bodyUrl = bodyUrl;
    this._errorUrl = errorUrl;
    this._dataLoader = dataLoader;
    this._elements = [];
    this._dataLoader.subscribe(this);
}

Grid.prototype.destroy = function() {
   this._tbody.empty();
   this._dataLoader.unsubscribe(this);
}

Grid.prototype.update = function(historyData) {
    //todo: validate
    this._elements = historyData.elements;
    this._redraw();
}

Grid.prototype._redraw = function() {
    this._tbody.empty();
    for(var i = 0; i < this._elements.length; i++) {
        var curHistoryElement = this._elements[i];
        var tableRecord = $("<tr></tr>");
        var tdPrefix = "<td>";
        var tdPostfix = "</td>";

        if(curHistoryElement.status == "fail") {
            tableRecord.addClass("danger_with_hover");
            //tdPrefix = "<td data-toggle=\"modal\" data-target=\"#error_modal\">";
        }

        var tds = this._toTdElements(tdPrefix, curHistoryElement, tdPostfix);
        delete tds.bodySize;
        if(curHistoryElement.status == "fail") {
            this._assignErrorHandler(tds, curHistoryElement.uuid);
        }
        this._appendTo(tds, tableRecord);

        if(curHistoryElement.bodySize == 0) {
            $(tdPrefix + curHistoryElement.bodySize + tdPostfix).appendTo(tableRecord);
        } else {
            var tdEmpty = $(tdPrefix + tdPostfix);
            var bodySize = $("<a href=\"#\" class=\"alert-link\">"
                                + curHistoryElement.bodySize + "</a>");

            bodySize.click(this._loadBody.bind(this, curHistoryElement.uuid));
            bodySize.appendTo(tdEmpty);
            tdEmpty.appendTo(tableRecord)
        }

        tableRecord.appendTo(this._tbody);
    }
}

Grid.prototype._toTdElements = function(tdPrefix, curHistoryElement, tdPostfix) {
    var result = {};
    for(var key in curHistoryElement) {
        result[key] = $(tdPrefix + curHistoryElement[key] + tdPostfix);
    }
    return result;
}

Grid.prototype._appendTo = function(tds, tableRecord) {
    tds.receivedTimestamp.appendTo(tableRecord);
    tds.uuid.appendTo(tableRecord);
    tds.durationOfProxy.appendTo(tableRecord);
    tds.sourceAddress.appendTo(tableRecord);
    tds.contentType.appendTo(tableRecord);
}

Grid.prototype._assignErrorHandler = function(tds, uuid) {
    for(var key in tds) {
        tds[key].click(this._loadError.bind(this, uuid));
    }
}

Grid.prototype._loadBody = function(uuid) {
    this._dataLoader.loadByUUID(this._bodyUrl, uuid, this._onLoadBodySucc.bind(this, this._modalBodyDiv, this._modalBody));
}

Grid.prototype._loadError = function(uuid, event) {
    this._dataLoader.loadByUUID(this._errorUrl, uuid, this._onLoadBodySucc.bind(this, this._modalErrorDiv, this._modalError));
}

Grid.prototype._onLoadBodySucc = function(modalDiv, modal, data) {
    modal.empty();

    var modalHeight = Math.floor(($(window).height()*3)/4);
    var textArea = $("<textarea readonly class=\"form-control\">" + data.toString() + "</textarea>")

    textArea.height(modalHeight);
    textArea.appendTo(modal);

    modalDiv.modal();
}