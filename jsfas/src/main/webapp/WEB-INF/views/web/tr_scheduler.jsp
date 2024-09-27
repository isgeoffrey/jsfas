<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@page import="jsfas.common.constants.AppConstants" %>
<%@page import="jsfas.common.constants.RestURIConstants" %>
<jsp:include page="common/header.jsp" />
	
<div class="container main-body" role="main">
    <div class="row">
        <div class="col-md-2">
            <div class="panel panel-info">
                <div class="panel-heading">
                    <h4 class="text-center">Configuration</h4>
                </div>
                <div class="panel-body">
                    <div class="form-group">
                        <div>
                            <label>Refresh interval:</label>
                        </div>
                        <div>
                            <select class="form-control input-sm" id="selRefreshInterval">
                                <option value="1000">1s</option>
                                <option value="5000">5s</option>
                                <option value="10000">10s</option>
                                <option value="15000">15s</option>
                                <option value="20000">20s</option>
                                <option value="25000">25s</option>
                                <option value="30000" selected="selected">30s</option>
                            </select>
                        </div>
                    </div>
                    <div class="form-group">
                        <button id="btnResumeAll" type="button" class="btn btn-primary btn-block">
                            <span class="glyphicon glyphicon-play"></span> Resume All
                        </button>
                    </div>
                    <div class="form-group">
                        <button id="btnPauseAll" type="button" class="btn btn-danger btn-block">
                            <span class="glyphicon glyphicon-pause"></span> Pause All
                        </button>
                    </div>
                </div>
            </div>
        </div>
        <div class="col-md-10">
            <div class="panel panel-info">
                <div class="panel-heading">
                    <div class="row">
                        <div class="col-sm-11">
                            <h4>${thisPage.pageTitleTxt}</h4>
                        </div>
                        <div class="col-sm-1">
                            <div class="pull-right" style="padding-top: 6px;">
                                <button type="button" id="btnAddTrigger" class="btn btn-info btn-sm">
                                    <span class="glyphicon glyphicon-plus"></span> Add
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="panel-body">
                    <div class="dataTable_wrapper">
                        <table class="table table-striped table-bordered table-hover" id="tbljobInfo">
                            <thead>
                                <tr>
                                    <th>Job Name</th>
                                    <th>Job Group</th>
                                    <th>Trigger Name</th>
                                    <th>Trigger Group</th>
                                    <th>Next Fire Time</th>
                                    <th>Previous Fire Time</th>
                                    <th>Trigger State</th>
                                    <th>Action</th>
                                </tr>
                            </thead>
                            <tbody>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<div id="divTriggerDetailsModal" class="modal fade" role="dialog">
    <div class="modal-dialog modal-lg">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title"></h4>
            </div>
            <div class="modal-body">
                <form id="frmTriggerDetails" class="form-horizontal">
                    <div class="form-group">
                        <div class="col-sm-2 text-right"><label>Job Group<span id="spanJobGroupLblMandFlag"><font class="mandatory-ind"> *</font></span>:</label></div>
                        <div class="col-sm-4">
                            <select id="selJobGroup" name="selJobGroup" class="form-control input-sm">
                            </select>
                        </div>
                        <div class="col-sm-2 text-right"><label>Job Name<span id="spanJobNameLblMandFlag"><font class="mandatory-ind"> *</font></span>:</label></div>
                        <div class="col-sm-4">
                            <select id="selJobName" name="selJobName" class="form-control input-sm">
                            </select>
                        </div>
                    </div>
                    <div class="form-group">
                        <div class="col-sm-2 text-right"><label>Trigger Name<span id="spanTriggerNameLbLMandFlag"><font class="mandatory-ind"> *</font></span>:</label></div>
                        <div class="col-sm-4">
                            <input id="inputTriggerName" name="inputTriggerName" type="text" class="form-control input-sm" maxlength="200" />
                        </div>
                        <div class="col-sm-2 text-right"><label>Next Fire Time<span id="spanTriggerStartTimeLblMandFlag"><font class="mandatory-ind"> *</font></span>:</label></div>
                        <div class="col-sm-4">
                            <input type="text" id="inputTriggerStartTime" name="inputTriggerStartTime" class="form-control input-sm" />
                        </div>
                    </div>
                    <input type="hidden" id="inputHiddenOldTriggerName" name="inputHiddenOldTriggerName" value="" />
                    <input type="hidden" id="inputHiddenActionType" name="inputHiddenActionType" value="" />
                </form>
            </div>
            <div class="modal-footer">
                <div class="btn-group">
                    <span class="panel-btn">
                        <button id="btnSaveTriggerDetails" type="button" class="btn btn-primary btn-sm" data-dismiss="modal">
                            <span class="glyphicon glyphicon-ok"></span> Save
                        </button>
                    </span>
                    <span class="panel-btn">
                        <button type="button" class="btn btn-primary btn-sm" data-dismiss="modal">
                            <span class="glyphicon glyphicon-remove"></span> Cancel
                        </button>
                    </span>
                </div>
            </div>
        </div>
    </div>
</div>
<div id="divConfirmModal" class="modal fade" role="dialog">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title"></h4>
            </div>
            <div class="modal-body">
                <div class="row">
                    <div class="col-sm-12">
                        <span id="spanConfirmMsg"></span>
                    </div>
                </div>
            </div>
            <div class="modal-footer">
                <div class="btn-group">
                    <span class="panel-btn">
                        <button id="btnConfirm" type="button" class="btn btn-primary btn-sm" data-dismiss="modal">
                            <span class="glyphicon glyphicon-ok">
                            </span> Confirm
                        </button>
                    </span>
                    <span class="panel-btn">
                        <button type="button" class="btn btn-default btn-sm" data-dismiss="modal">
                            <span class="glyphicon glyphicon-remove">
                                </span> Cancel
                        </button>
                    </span>
                </div>
            </div>
        </div>
    </div>
</div>
<script>
/* DOM ready */
$(document).ready(function() {
	/* Pre-defined data */
	var jobInfoMapStr = '<spring:message text="${jobInfoMap}" javaScriptEscape="true"/>';
	var jobInfoMap = $.trim(jobInfoMapStr).length !== 0? JSON.parse(jobInfoMapStr): {};
	var timerId = undefined;
	/* Utility helpers */
	var tblJobInfoActionColRenderer = function tblJobInfoActionColRenderer(data) {
		var exeBtnData = undefined;
		var chgBtnData = undefined;
        var remBtnData = undefined;
		
		var $btn = undefined;
		var $div = $('<div />');
		
		if($.isPlainObject(data)) {
			exeBtnData = data['exeBtnData'];
			chgBtnData = data['chgBtnData'];
			remBtnData = data['remBtnData'];
			
			$.each([exeBtnData, chgBtnData, remBtnData], function(i, btnData){
				$btn = $('<span class="panel-btn"></span>');
				switch(btnData['actType']) {
				case '${AppConstants.EXE_FUNC_SUB_CDE}': /* Execute */
				    switch(btnData['triggerState']) {
				    case '${AppConstants.SCHEDULER_TRIGGER_STATE_PAUSED}':
				    case '${AppConstants.SCHEDULER_TRIGGER_STATE_ERROR}':
				    	$btn.append($('<button type="button" class="ust btn btn-primary btn-circle" data-toggle="tooltip" data-placement="auto" title="Resume"></button>')
	                        .append($('<i class="glyphicon glyphicon-play"><i/>')).tooltip());
				    	break;
				    case '${AppConstants.SCHEDULER_TRIGGER_STATE_BLOCKED}':
				    case '${AppConstants.SCHEDULER_TRIGGER_STATE_NORMAL}':
				    case '${AppConstants.SCHEDULER_TRIGGER_STATE_COMPLETE}':
				    	$btn.append($('<button type="button" class="ust btn btn-danger btn-circle" data-toggle="tooltip" data-placement="auto" title="Pause"></button>')
	                        .append($('<i class="glyphicon glyphicon-pause"><i/>')).tooltip());
				    	break;
				    default:
				    	$btn.addClass('invisible');
				    }
				    break;
				case '${AppConstants.CHG_FUNC_SUB_CDE}':
					$btn.append($('<button type="button" class="ust btn btn-warning btn-circle" data-toggle="tooltip" data-placement="auto" title="Change"></button>')
                        .append($('<i class="glyphicon glyphicon-pencil"><i/>')).tooltip());
					break;
				case '${AppConstants.REM_FUNC_SUB_CDE}':
					$btn.append($('<button type="button" class="ust btn btn-danger btn-circle" data-toggle="tooltip" data-placement="auto" title="Remove"></button>')
	                    .append($('<i class="glyphicon glyphicon-trash"><i/>')).tooltip());
					break;
				default:
					throw 'Unsupported action type=' + btnData['actType'];
				}
				if(btnData['visible']) {
					$btn.find('button').on('click', {
						'jobName': btnData['jobName'],
						'jobGroup': btnData['jobGroup'],
	                    'triggerName': btnData['triggerName'],
	                    'triggerGroup': btnData['triggerGroup'],
	                    'nextFireTime': btnData['nextFireTime'],
	                    'actType': btnData['actType']
	                }, clickBtnJobInfoActionEvtHndler).prop('disabled', !btnData['enable']);
				} else {
                    $btn.addClass('invisible');
                }
				
				$div.append($btn);
			});
		}
		return $div;
	};
	
	var tblJobInfoAjaxRespHndler = function tblJobInfoAjaxRespHndler(json) {
		var data = json['data'];
		var dataSet = [];
		var btnData = undefined;
		var exeBtnData = undefined;
		var chgBtnData = undefined;
		var remBtnData = undefined;
		clearAlert('all');
		if(json['status'] !== '${AppConstants.RESPONSE_JSON_SUCCESS_CODE}') {
			addAlert('danger', json['message']);
		} else {
			$.each(data, function(i, item) {
				btnData = {'triggerState': item['triggerState'],
						   'triggerName': item['triggerName'],
						   'triggerGroup': item['triggerGroup'],
						   'jobName': item['jobName'],
						   'jobGroup': item['jobGroup'],
						   'nextFireTime': item['nextFireTime']};
				
				exeBtnData = $.extend({'actType': '${AppConstants.EXE_FUNC_SUB_CDE}', 'visible': true, 'enable': true}, btnData);
				chgBtnData = $.extend({'actType': '${AppConstants.CHG_FUNC_SUB_CDE}', 'visible': false, 'enable': false}, btnData);
				remBtnData = $.extend({'actType': '${AppConstants.REM_FUNC_SUB_CDE}', 'visible': false, 'enable': false}, btnData);
				
				if(item['triggerGroup'] === '${AppConstants.SCHEDULER_ADHOC_SIMPLE_TRIGGER_GROUP}') {
					chgBtnData['visible'] = true;
					remBtnData['visible'] = true;
					
					if(item['triggerState'] === '${AppConstants.SCHEDULER_TRIGGER_STATE_PAUSED}'
							|| item['triggerState'] === '${AppConstants.SCHEDULER_TRIGGER_STATE_ERROR}') {
						chgBtnData['enable'] = true;
	                    remBtnData['enable'] = true;
					}
				}
				
				dataSet.push([item['jobName'],
					          item['jobGroup'],
					          item['triggerName'],
					          item['triggerGroup'],
					          item['nextFireTime'],
					          item['prevFireTime'],
					          item['triggerState'],
					          {'exeBtnData': exeBtnData, 'chgBtnData': chgBtnData, 'remBtnData': remBtnData}]);
			});
		}
        return dataSet;
	};
	/* Event handlers */
	var clickBtnSaveTriggerDetailsEvtHndler = function clickBtnSaveTriggerDetailsEvtHndler(e) {
		var $frmTriggerDetails = $('#frmTriggerDetails');
		var $selJobGroup = $('#selJobGroup');
		var $selJobName = $('#selJobName');
		var $inputTriggerName = $('#inputTriggerName');
		var $inputTriggerStartTime = $('#inputTriggerStartTime');
		var $inputHiddenOldTriggerName = $('#inputHiddenOldTriggerName');
		var $inputHiddenActionType = $('#inputHiddenActionType');
		var api = '${pageContext.request.contextPath}${RestURIConstants.ADD_SCHEDULER_TRIGGER}';
		
		if(!$frmTriggerDetails.valid()) {
			$(this).one('click', clickBtnSaveTriggerDetailsEvtHndler);
			return false;
		}
		
		if($inputHiddenActionType.val() === '${AppConstants.CHG_FUNC_SUB_CDE}') {
			api = '${pageContext.request.contextPath}${RestURIConstants.CHG_SCHEDULER_TRIGGER}';
		}
		
		commonAjax({'jobGroup': $selJobGroup.val(), 
			        'jobName': $selJobName.val(), 
			        'triggerName': $inputTriggerName.val(), 
			        'oldTriggerName': $inputHiddenOldTriggerName.val(),
			        'nextFireTime': $inputTriggerStartTime.val()},
				   api,
                   function() {
                       if($.fn.DataTable.isDataTable('#tbljobInfo')) {
                           $('#tbljobInfo').dataTable().api().ajax.reload();
                       }
                   },
                   null,
                   null,
                   '${pageContext.request.contextPath}${RestURIConstants.SCHEDULER_PATH}',
                   false);
	};
	
	var clickBtnConfirmRemoveTriggerEvtHndler = function clickBtnConfirmRemoveTriggerEvtHndler(e) {
		var data = e['data'];
		
		if($.isEmptyObject(data)) {
			throw 'Invalid arguments=' + JSON.stringify(data);
		}
		
		commonAjax(data, 
				   '${pageContext.request.contextPath}${RestURIConstants.REM_SCHEDULER_TRIGGER}',
                   function() {
                       if($.fn.DataTable.isDataTable('#tbljobInfo')) {
                           $('#tbljobInfo').dataTable().api().ajax.reload();
                       }
                   },
                   null,
                   null,
                   '${pageContext.request.contextPath}${RestURIConstants.SCHEDULER_PATH}',
                   false);
	};
	
	var clickBtnJobInfoActionEvtHndler = function clickBtnJobInfoActionEvtHndler(e) {
		var data = e['data'];
		var api = undefined;
		
		var $divTriggerDetailsModal = $('#divTriggerDetailsModal');
		var $divTriggerDetailsModalTitle = $('.modal-title', $divTriggerDetailsModal);
		var $selJobGroup = $('#selJobGroup', $divTriggerDetailsModal);
		var $selJobName = $('#selJobName', $divTriggerDetailsModal);
		var $btnSaveTriggerDetails = $('#btnSaveTriggerDetails', $divTriggerDetailsModal);
		
		var $divConfirmModal = $('#divConfirmModal');
		var $divConfirmModalTitle = $('.modal-title', $divConfirmModal);
		var $spanConfirmMsg = $('#spanConfirmMsg', $divConfirmModal);
		var $btnConfirm = $('#btnConfirm', $divConfirmModal);
		
		if($.isEmptyObject(data)) {
            throw 'Invalid arguments=' + data;
        }
		
		switch(data['actType']) {
		case '${AppConstants.EXE_FUNC_SUB_CDE}':
			if($(this).find('i').hasClass('glyphicon-pause')) {
	            api = '${pageContext.request.contextPath}${RestURIConstants.PAUSE_SCHEDULER_TRIGGER}';
	        } else {
	        	api = '${pageContext.request.contextPath}${RestURIConstants.RESUME_SCHEDULER_TRIGGER}';
	        }
			commonAjax(data, 
	                   api,
	                   function() {
	                       if($.fn.DataTable.isDataTable('#tbljobInfo')) {
	                           $('#tbljobInfo').dataTable().api().ajax.reload();
	                       }
	                   },
	                   null,
	                   null,
	                   '${pageContext.request.contextPath}${RestURIConstants.SCHEDULER_PATH}',
	                   false);
			break;
		case '${AppConstants.ADD_FUNC_SUB_CDE}':
			$divTriggerDetailsModalTitle.empty().text('Add Adhoc Trigger');
			$divTriggerDetailsModal.find('input, select').val('');
			$divTriggerDetailsModal.autofill({
				'inputHiddenActionType': data['actType']
			});
			$.each([$selJobGroup, $selJobName], function(i, elem) {
                $(elem).prop('disabled', false);
            });
			$btnSaveTriggerDetails.one('click', clickBtnSaveTriggerDetailsEvtHndler);
			$divTriggerDetailsModal.modal('show');
			break;
		case '${AppConstants.CHG_FUNC_SUB_CDE}':
			$divTriggerDetailsModalTitle.empty().text('Edit Adhoc Trigger');
			$divTriggerDetailsModal.autofill({
				'selJobGroup': data['jobGroup'],
				'inputTriggerName': data['triggerName'],
				'inputTriggerStartTime': data['nextFireTime'],
				'inputHiddenOldTriggerName': data['triggerName'],
                'inputHiddenActionType': data['actType']
            });
			$('#selJobGroup').trigger('change');
			$('#selJobName').val(data['jobName']);
			$.each([$selJobGroup, $selJobName], function(i, elem) {
				$(elem).prop('disabled', true);
			});
			$btnSaveTriggerDetails.one('click', clickBtnSaveTriggerDetailsEvtHndler);
            $divTriggerDetailsModal.modal('show');
			break;
		case '${AppConstants.REM_FUNC_SUB_CDE}':
			$divConfirmModalTitle.empty().text('Remove Adhoc Trigger');
			$spanConfirmMsg.text('Are you sure you want to remove trigger "' + data['triggerGroup'] + '.' + data['triggerName'] + '" ?');
			$btnConfirm.one('click', {'triggerName': data['triggerName']}, clickBtnConfirmRemoveTriggerEvtHndler);
			$divConfirmModal.modal('show');
			break;
		}
	};
	/* Init routine */
	var $btnResumeAll = $('#btnResumeAll');
	var $btnPauseAll = $('#btnPauseAll');
	var $selRefreshInterval = $('#selRefreshInterval');
	var $btnAddTrigger = $('#btnAddTrigger');
	var $inputTriggerStartTime = $('#inputTriggerStartTime');
	var $divTriggerDetailsModal = $('#divTriggerDetailsModal');
	var $frmTriggerDetails = $('#frmTriggerDetails');
	var $selJobGroup = $('#selJobGroup');
	var $selJobName = $('#selJobName');
	var $inputTriggerName = $('#inputTriggerName');
	var $inputTriggerStartTime = $('#inputTriggerStartTime');
	
	if(!$.isEmptyObject(jobInfoMap)) {
		$.each(jobInfoMap, function(jobGroup) {
			$selJobGroup.append($('<option />', {
				'text': jobGroup,
				'value': jobGroup
			}));
		});
	}
	
	$inputTriggerStartTime.datetimepicker({
        'format': 'dd/mm/yyyy hh:ii:ss',
        'startDate': new Date(),
        'autoclose': true,
        'todayBtn': 'linked',
        'todayHighlight': true,
        'minuteStep': 1
    });
	
	$frmTriggerDetails.validate();
	
	$selJobGroup.rules('add', {
		'required': {
			'depends': function() {
				var $spanJobGroupLblMandFlag = $('#spanJobGroupLblMandFlag');
				return $spanJobGroupLblMandFlag.is(':visible') && $(this).is(':enabled');
			}
		},
		'normalizer': function(value) {
			return $.trim(value);
		}
	});
	
	$selJobName.rules('add', {
		'required': {
			'depends': function() {
				var $spanJobNameLblMandFlag = $('#spanJobNameLblMandFlag');
				return $spanJobNameLblMandFlag.is(':visible') && $(this).is(':enabled');
			}
		},
		'normalizer': function(value) {
			return $.trim(value);
		}
	});
	
	$inputTriggerName.rules('add', {
		'required': {
			'depends': function() {
				var $spanTriggerNameLbLMandFlag = $('#spanTriggerNameLbLMandFlag');
				return $spanTriggerNameLbLMandFlag.is(':visible') && $(this).is(':enabled');
			}
		},
		'normalizer': function(value) {
			return $.trim(value);
		}
	});
	
	$inputTriggerStartTime.rules('add', {
		'required': {
			'depends': function() {
				var $spanTriggerStartTimeLblMandFlag = $('#spanTriggerStartTimeLblMandFlag');
				return $spanTriggerStartTimeLblMandFlag.is(':visible') && $(this).is(':enabled');
			}
		},
		'normalizer': function(value) {
			return $.trim(value);
		}
	});
	
	/* DOM event setup */
	$selRefreshInterval.on('change', function() {
		clearInterval(timerId);
		setInterval(function() {
			if($.fn.DataTable.isDataTable('#tbljobInfo')) {
	            $('#tbljobInfo').dataTable().api().ajax.reload();
	        }
		}, parseInt($(this).val(), 10));
	}).trigger('change');
	
	$selJobGroup.on('change', function() {
		$selJobName.find('option').remove();
		if($.type(jobInfoMap[$(this).val()]) === 'array') {
			$.each(jobInfoMap[$(this).val()], function(i, jobName) {
				$selJobName.append($('<option />', {
					'text': jobName,
					'value': jobName
				}));
			});
		}
	});
	
	$btnAddTrigger.on('click', {'actType': '${AppConstants.ADD_FUNC_SUB_CDE}'}, clickBtnJobInfoActionEvtHndler);
	
	$divTriggerDetailsModal.on('shown.bs.modal', function() {
		var $inputHiddenActionType = $('#inputHiddenActionType', $(this));
		
		if($inputHiddenActionType.val() === '${AppConstants.CHG_FUNC_SUB_CDE}') {
			$frmTriggerDetails.valid();
		} else {
			$frmTriggerDetails.validate().resetForm();
		}
	});
	
	$btnPauseAll.on('click', function() {
        commonAjax({}, 
                '${pageContext.request.contextPath}${RestURIConstants.PAUSE_SCHEDULER_ALL_TRIGGER}',
                function() {
                    if($.fn.DataTable.isDataTable('#tbljobInfo')) {
                        $('#tbljobInfo').dataTable().api().ajax.reload();
                    }
                },
                null,
                null,
                '${pageContext.request.contextPath}${RestURIConstants.SCHEDULER_PATH}',
                false);
    });
	
	$btnResumeAll.on('click', function() {
        commonAjax({}, 
                   '${pageContext.request.contextPath}${RestURIConstants.RESUME_SCHEDULER_ALL_TRIGGER}',
                   function() {
                       if($.fn.DataTable.isDataTable('#tbljobInfo')) {
                           $('#tbljobInfo').dataTable().api().ajax.reload();
                       }
                   },
                   null,
                   null,
                   '${pageContext.request.contextPath}${RestURIConstants.SCHEDULER_PATH}',
                   false);
    });
	
	/* Post routine */
	$('#tbljobInfo').DataTable({
		'columnDefs': [
			{'name': 'jobName', 'targets': 0},
			{'name': 'jobGroup', 'targets': 1},
			{'name': 'triggerName', 'targets': 2},
			{'name': 'triggerGroup', 'targets': 3},
			{'name': 'nextFireTime', 'targets': 4},
			{'name': 'prevFireTime', 'targets': 5},
			{'name': 'triggerState', 'targets': 6},
			{'searchable': false, 
			 'orderable': false, 
			 'createdCell': function(cell, cellData) {
				 $(cell).empty().append(tblJobInfoActionColRenderer(cellData));
			 },
			 'targets': 7}
		],
		'serverSide': true,
		'ajax': {
			'beforeSend': function(jqXHR) {
				jqXHR.setRequestHeader('${_csrf.headerName}', '${_csrf.token}');
            },
			'url': '${pageContext.request.contextPath}${RestURIConstants.GET_SCHEDULER_JOB_LIST}',
			'type': 'POST',
			'dataType': 'json',
            'contentType': 'application/json; charset=utf-8',
			'data': function(data, settings) {
				data['search'] = data['search']['value'];
				data['sort'] = data['order'].reduce(function(a, b) {
                    var columns = data['columns'];
                    var columnName = columns[b['column']]['name'];
                    
                    return a.concat(columnName + ',' + b['dir']);
                }, []);
				
				if(data['columns']) {
                    delete data['columns'];
                }
				
                if(data['order']) {
                    delete data['order'];
                }
                
                return JSON.stringify(data);
			},
			'dataSrc': tblJobInfoAjaxRespHndler,
			'error': function(jqXHR, textStatus, errorThrown) {
				if(jqXHR['responseText'] && jqXHR['responseText'].indexOf('Access Denied') !== -1) {
                    refreshPage();
                    return false;
                }
                window.location.href = $("meta[name='_cas_expire_url']").attr("content");
			}
		}
	});
});
</script>

<jsp:include page="common/footer.jsp" />