<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

	<!-- JavaScript --> <%-- put all js on footer to make page load faster --%>
	
	<!-- Bootstrap Core JavaScript -->
    <script src="${pageContext.request.contextPath}/resources/js/bootstrap.min.js?v=${cacheVersion.staticVer}"></script>
    
	<!-- DataTables JavaScript -->
	<script src="${pageContext.request.contextPath}/resources/js/jquery.dataTables.min.js?v=${cacheVersion.staticVer}"></script>
    <script src="${pageContext.request.contextPath}/resources/js/dataTables.bootstrap.min.js?v=${cacheVersion.staticVer}"></script>
    <script src="${pageContext.request.contextPath}/resources/js/dataTables.responsive.min.js?v=${cacheVersion.staticVer}"></script>
    <script src="${pageContext.request.contextPath}/resources/js/dataTables.select.js?v=${cacheVersion.staticVer}"></script>
	
	<!-- Bootstrap Date Picker -->
    <script src="${pageContext.request.contextPath}/resources/js/bootstrap-datepicker.min.js?v=${cacheVersion.staticVer}"></script>
	
    <!-- Bootstrap Date Time Picker -->
    <script src="${pageContext.request.contextPath}/resources/js/bootstrap-datetimepicker.min.js?v=${cacheVersion.staticVer}"></script>
    
    <!-- Bootstrap Autocomplete Table -->
    <script src="${pageContext.request.contextPath}/resources/js/tautocomplete.bootstrap.min.js?v=${cacheVersion.staticVer}"></script>
    
	<!-- jQuery Validation Plugin -->
    <script src="${pageContext.request.contextPath}/resources/js/jquery.validate.min.js?v=${cacheVersion.staticVer}"></script>
    
    <!-- Escapify JQuery Plugin -->
    <script src="${pageContext.request.contextPath}/resources/js/jquery.escapify.js?v=${cacheVersion.staticVer}"></script>
    
    <!-- jQuery Form AutoFill Plugin -->
    <script src="${pageContext.request.contextPath}/resources/js/jquery.formautofill.min.js?v=${cacheVersion.staticVer}"></script>
    
    <!-- jQuery zTree Plugin -->
    <script src="${pageContext.request.contextPath}/resources/js/jquery.ztree.all.min.js?v=${cacheVersion.staticVer}"></script>
    
    <!-- jQuery zTree Extension Hide Nodes Plugin -->
    <script src="${pageContext.request.contextPath}/resources/js/jquery.ztree.exhide.min.js?v=${cacheVersion.staticVer}"></script>
	
	<!-- Common js -->
    <script src="${pageContext.request.contextPath}/resources/js/common.js?v=${cacheVersion.staticVer}"></script>
    
    <!-- Custom JavaScript -->
    <script src="${pageContext.request.contextPath}/resources/js/jsfas.js?v=${cacheVersion.staticVer}"></script>
    
    <script>
    <c:if test="${activeMenuItem != null}">
    	$("#${activeMenuItem}").addClass("active");
    	$("#${activeMenuItem}").parents("li").addClass("active");
    </c:if>
	
	// override jquery validate plugin defaults
    $.validator.setDefaults({
    	highlight: function(element) {
            if($(element).parent('.input-group').length) {
                $(element).parent().parent().addClass('has-error')
            } else {
                $(element).parent().addClass('has-error');
            }
        },
        unhighlight: function(element) {
            if($(element).parent('.input-group').length) {
                $(element).parent().parent().removeClass('has-error');
            } else {
                $(element).parent().removeClass('has-error');
            }
        },
        errorElement: 'span',
        errorClass: 'help-block',
        errorPlacement: function(error, element) {
            if(element.parent('.input-group').length) {
                error.insertAfter(element.parent());
            } else {
                error.insertAfter(element);
            }
        }
    });
	
    // override jquery validate plugin resetForm()
    $.validator.prototype.resetForm = function() {
        if($.fn.resetForm) {
            $(this.currentForm).resetForm();
        }
        var thisForm = $(this.currentForm);
        this.prepareForm();
        this.hideErrors();
        var elements = $(this.currentForm).find(".has-error");
        if(this.settings.unhighlight && elements.length !== 0) {
            for (var i = 0; elements[i]; i++) {
                if($(elements[i]).children().length !== 0) {
                    this.settings.unhighlight.call(this, $(elements[i]).children()[0], this.settings.errorClass, this.settings.validClass);
                } else {
                    $(elements[i]).removeClass('has-error');
                }
            }
        }
    }
	
	// override jquery validate plugin checkForm()
    $.validator.prototype.checkForm = function() {
        this.prepareForm();
        for (var i = 0, elements = (this.currentElements = this.elements()); elements[i]; i++) {
            // extension here to support multiple element with same name validation
            if (this.findByName(elements[i].name).length !== undefined && this.findByName(elements[i].name).length > 1) {
                for (var cnt = 0; cnt < this.findByName(elements[i].name).length; cnt++) {
                    this.check(this.findByName(elements[i].name)[cnt]);
                }
            } else {
                this.check(elements[i]);
            }
        }
        return this.valid();
    };
	
	//custom vaildation methods
	$.validator.addMethod("pattern", function(value, element, params) {
		return this.optional( element ) || params.test( value );
	}, $.validator.format("Please enter a valid value."));
	
	// override bootstrap setScrollBar
	$.fn.modal.Constructor.prototype.setScrollbar = function () {
		this.$body.css('padding-right', '');
	};
	
	// override bootstrap adjustDialog
	$.fn.modal.Constructor.prototype.adjustDialog = function () {
		this.$element.css({
			paddingLeft:  '',
			paddingRight: ''
		});
	};
	
	// override bootstrap hideModal
	$.fn.modal.Constructor.prototype.hideModal = function () {
		var that = this;
		this.$element.hide();
		this.backdrop(function () {
			if($(".modal.in").length == 0 || $(".modal.in").not("#waitingDialog").length == 0) {
				that.$body.removeClass('modal-open');
			}
			that.resetAdjustments();
			that.resetScrollbar();
			that.$element.trigger('hidden.bs.modal');
			
		});
	}
	
	//alert fixed position script
	var stickyOffset = $("#alertBox").offset().top - 62;
	$(window).scroll(function(){
		$("#alertBoxWrapper").height($("#alertBox").outerHeight());
		if ($(window).scrollTop() >= stickyOffset) {
			if (!$("#alertBox").hasClass("alert-fixed")) {
				$("#alertBox").addClass("alert-fixed")
			}
		} else {
			$("#alertBox").removeClass("alert-fixed")
		}
	});
    </script>

</body>

</html>