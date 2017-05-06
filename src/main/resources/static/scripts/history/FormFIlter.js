

function FormFilter(id, dataLoader, pageSize) {
    this._id = id;
    this._pageSize = pageSize;
    this._dataLoader = dataLoader;
    this._mainForm = $("#" + this._id);
    this._datetimepickerBegin = $("#" + this._id + " input:text").first();
    this._datetimepickerEnd = $("#" + this._id + " input:text").last();
    this._submit = $("#" + this._id + " :submit");
    this._init();
}

FormFilter.prototype._DATE_FORMAT = "YYYY-MM-DD";
FormFilter.prototype._TIME_FORMAT = "HH:mm:ss";
FormFilter.prototype._DATE_TIME_FORMAT = FormFilter.prototype._DATE_FORMAT + " " + FormFilter.prototype._TIME_FORMAT;

FormFilter.prototype.destroy = function() {
    this._datetimepickerBegin.datetimepicker('destroy');
    this._datetimepickerEnd.datetimepicker('destroy');
}

FormFilter.prototype._init = function() {
    $.datetimepicker.setLocale('ru');
    $.datetimepicker.setDateFormatter({
        parseDate: function (date, format) {
            var d = moment(date, format);
            return d.isValid() ? d.toDate() : false;
        },

        formatDate: function (date, format) {
            return moment(date).format(format);
        }
    });

    var hourEarlier = moment().subtract(1, 'hour');
    var datetimepickerBegin = this._datetimepickerBegin;
    var datetimepickerEnd = this._datetimepickerEnd;
    this._datetimepickerBegin.datetimepicker({
                lang:'ru',
                step: 10,
                format: FormFilter.prototype._DATE_TIME_FORMAT,
                formatDate: FormFilter.prototype._DATE_FORMAT,
                formatTime: FormFilter.prototype._TIME_FORMAT,
                onChangeDateTime: this._onChangeDateTime.bind(this),
                onShow: function(ct) {
                    this.setOptions({
                        maxDate: datetimepickerEnd.val() ?
                                    moment(datetimepickerEnd.val()).format(FormFilter.prototype._DATE_FORMAT) :
                                    false
                    });
                }
    });
    this._datetimepickerBegin.val(hourEarlier.format(this._DATE_TIME_FORMAT));

    var now = moment().add(1, 'minute');
    this._datetimepickerEnd.datetimepicker({
                lang:'ru',
                step: 10,
                format: FormFilter.prototype._DATE_TIME_FORMAT,
                formatDate: FormFilter.prototype._DATE_FORMAT,
                formatTime: FormFilter.prototype._TIME_FORMAT,
                onChangeDateTime: this._onChangeDateTime.bind(this),
                onShow: function(ct) {
                    this.setOptions({
                        minDate: datetimepickerBegin.val() ?
                                    moment(datetimepickerBegin.val()).format(FormFilter.prototype._DATE_FORMAT) :
                                    false
                    });
                }
    });
    this._datetimepickerEnd.val(now.format(this._DATE_TIME_FORMAT));

    this._mainForm.submit(FormFilter.prototype._submit.bind(this));
}

FormFilter.prototype._onChangeDateTime = function() {
    if(this._datetimepickerBegin.val() &&
            this._datetimepickerEnd.val() &&
            moment(this._datetimepickerBegin.val()).isBefore(this._datetimepickerEnd.val())) {
        this._submit.removeClass("disabled");
        return;
    }
    this._submit.addClass("disabled");
}

FormFilter.prototype.getCurrentFilterObject = function() {
    return {
        from: moment(this._datetimepickerBegin.val()).format(this._DATE_TIME_FORMAT),
        to: moment(this._datetimepickerEnd.val()).format(this._DATE_TIME_FORMAT),
        pageNumber: 1,
        pageSize: this._pageSize
    };
}

FormFilter.prototype._submit = function(event) {
    event.preventDefault();
    if(!this._submit.hasClass("disabled")) {
        this._dataLoader.loadData(this.getCurrentFilterObject());
    }
}