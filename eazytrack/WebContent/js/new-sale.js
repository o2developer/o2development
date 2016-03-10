/**
 * Routines used in new-sale.html file are available here...
 */

/**
 * Gets the next available invoice number for creating a new sale txn. Adds
 * /displays this number in the given element.
 * 
 * @param {Object}
 *            element
 */

function getNextInvoiceNum(element, type) {
	var opcode = '';
	if (type == 'sale') {
		opcode = 4;
	} else {
		opcode = 5;
	}
	$.ajax({
		url : "/eazytrack/EzTrackServlet?opcode=" + opcode + "",
		// url : "responses/fetch-next-invoice.json",
		dataType : "json",
		success : function(data) {
			element.val(data[0].invoicenum); // invoicenum
		}
	});
}

/**
 * Transforms given data to a format (called datum) recognized by typeahead.
 * 
 * @param {Object}
 *            data
 */
function transformCustomers2Datum(data) {
	for (var i = 0; i < data.length; i++) {
		data[i].value = data[i].name;
		data[i].tokens = [ data[i].name, data[i].phone1 ];
	}
	// console.log("transformed data: " + JSON.stringify(data));
	return data;
}

/**
 * Converts items list data to a format recognized by typeahead.
 * 
 * @param {Object}
 *            data
 */
function transformItems2Datum(data) {
	for (var i = 0; i < data.length; i++) {
		data[i].value = data[i].name;
		data[i].tokens = [ data[i].name, data[i].category, data[i].code ];
	}
	console.log("transformed data: " + JSON.stringify(data));
	return data;
}

/**
 * Loads a row with item details.
 * 
 * @param {Object}
 *            datum
 * @param {Object}
 *            row
 */
function populateRow(row, datum) {
	// row.find(".desc span span:first").text(datum.name);
	row.find(".desc input.desc").typeahead('setQuery', datum.name);
	row.find(".qty input").val(1);
	row.find(".rate").text(datum.rate);
	// row.find(".disc-prcnt").text(datum.discnt_percent);
	row.find(".disc-prcnt input").val(datum.discnt_percent);

	if ($("#cform").is(":checked")) {
		row.find(".vat").text("2");
	} else {
		row.find(".vat").text(datum.vat);
	}
	row.data("item-info", datum);
	updateRow(row);
}

/**
 * Populates the row for given values of invoice
 * 
 * @param {Object}
 *            datum
 * @param {Object}
 *            row
 */
function populateRowEdit(row, datum) {
	// row.find(".desc span span:first").text(datum.name);
	var slno = row.prev().length ? parseInt(row.prev().find("td.sl-no").text()) + 1
			: 1;
	slno = datum.sl_no;
	row.find("td.sl-no").text(slno);
	row.find(".desc input.desc").typeahead('setQuery', datum.name);
	row.find(".qty input").val(datum.qty);
	row.find(".rate").text(datum.rate);
	// row.find(".disc-prcnt").text(datum.discnt_percent);
	row.find(".disc-prcnt input").val(datum.discnt_percent);

	if ($("#cform").is(":checked")) {
		row.find(".vat").text("2");
	} else {
		row.find(".vat").text(datum.vat);
	}
	row.data("item-info", datum);
	updateRow(row);
}

/**
 * computes calculated fields viz., discount value, amount
 * 
 * @param {Object}
 *            row
 * @param {object}
 *            recalc - boolean indicating whether to recalculate totals.
 */
function updateRow(row, recalc) {
	var quantity = parseInt(row.find(".qty input").val());
	var rate = (parseFloat(row.find(".rate").text())).toFixed(2);
	var discountPercent = parseFloat(row.find(".disc-prcnt input").val());
	var discount = (quantity * rate * discountPercent / 100).toFixed(2);
	var vatPercent = parseFloat(row.find(".vat").text());
	vatPercent = isNaN(vatPercent) ? 0 : vatPercent;
	var mrp = quantity * rate;
	// var amt = (baseAmt - (discount) + (baseAmt * vatPercent /
	// 100)).toFixed(2);

	var amtBeforeTax = (mrp - discount) * (100) / (100 + vatPercent);

	row.find(".rate").text(rate);
	row.find(".disc-val").text(discount);
	row.find(".amount").text(amtBeforeTax.toFixed(2));

	if (recalc == undefined || recalc == true) {
		updateTotals();
	}

}

/**
 * computes the sum of amounts of all item rows and updates the totals row[s].
 */
function updateTotals() {
	var rows = $("table#item-details tbody tr");
	var sum = 0;
	var disc_sum = 0;
	var tax_sum = 0;
	var qty_sum = 0;

	for (var i = 0; i < rows.length; i++) {
		var rowAmount = parseFloat($(rows[i]).find("td.amount").text());
		sum += isNaN(rowAmount) ? 0 : rowAmount;

		var rowDiscount = parseFloat($(rows[i]).find(".disc-val").text());
		disc_sum += isNaN(rowDiscount) ? 0 : rowDiscount;

		var tax_val = rowAmount * parseFloat($(rows[i]).find(".vat").text())
				/ 100;
		tax_val = isNaN(tax_val) ? 0 : tax_val;
		tax_sum += tax_val;

		var qty_val = parseInt($(rows[i]).find("td.qty input").val());
		qty_val = isNaN(qty_val) ? 0 : qty_val;
		qty_sum += qty_val;
	}

	// first footer row
	$("#disc-total").text(disc_sum.toFixed(2));
	$("#tax-total").text(tax_sum.toFixed(2));
	$("#base-total").text(sum.toFixed(2));
	$("#qty-total").text(qty_sum);

	// 2nd footer row (amount after application of taxes)
	var amtWithTaxes = (sum + tax_sum);
	$("#amt-with-taxes").text(amtWithTaxes.toFixed(2));

	// 3rd footer row (amount after discount)
	// var amtAfterDiscount = amtWithTaxes - disc_sum;
	// $("#amt-after-discount").text(amtAfterDiscount.toFixed(2));

	// 4th row (additional charges)
	var charges = parseFloat($("#addnl-charges input").val());
	charges = isNaN(charges) ? 0 : charges;
	$("#addnl-charges input").val(charges.toFixed(2));

	// 5th row (roundoff value)
	var roundoff = parseFloat($("#roundoff input").val());
	roundoff = isNaN(roundoff) ? 0 : roundoff;
	$("#roundoff input").val(roundoff.toFixed(2));

	// 6th last row (final total)
	// sum += charges - roundoff;
	var finalAmt = amtWithTaxes + charges - roundoff;
	$("#grand-total").text(finalAmt.toFixed(2));
}

/**
 * Removes all content of a given item row.
 * 
 * @param {Object}
 *            row
 */
function clearRow(row) {
	var slno = row.prev().length ? parseInt(row.prev().find("td.sl-no").text()) + 1
			: 0;
	row.find("td.sl-no").text(slno);
	row.find("td.desc input").val("");
	row.find("td.qty input").val("");
	row.find("td.rate").text("");
	row.find("td.disc-prcnt input").val("");
	row.find("td.disc-val").text("");
	row.find("td.vat").text("");
	row.find("td.amount").text("");
}

/**
 * Used for binding typeahead functionality for new rows created on the fly.
 * @param {Object}
 * element
 */
function bindTypeAhead(element) {
	element.parent().find("input.tt-hint").remove();
	var url = getRequestUrl(3);
	element.typeahead({
				name : "itemsList",
				remote : url,
				template : [
						"<p class='category-hint'>{{category}}&nbsp;&gt; &nbsp;</p>",
						"<p class='code-hint'>{{code}}&nbsp;&gt;&nbsp;</p>",
						"<p class='name-hint'>{{name}}</p>" ].join(""),
				header : "<div class='bottom-border hint-header'><h5>Category &gt; Item Code &gt; Name</h5></div>",
				engine : Hogan,
				limit : 10,
			});

	$(element).bind('typeahead:selected', function(obj, datum, name) {
		console.log("Item selected.");
		populateRow($(this).parents("tr"), datum);
		$(this).parents("tr").data("data-filled", true);
		var qtyField = $(this).parents("tr").find("td.qty input");
		qtyField.attr('ext', "true");
		$(this).parents("tr").find("td.qty input").focus().select();

	});
}

/**
 * Prepares a JSON object consisting of all user entered values for creating a
 * new sale txn.
 */
function prepareSaleJson(action) {
	var result = new Object();
	  debugger ;
	/** Overall invoice data * */
	result.invoice = new Object();
	result.invoice.invoice_no = $("#invoice-no").val();
	result.invoice.invoice_date = convert2outputDt($("#date").text());
	result.invoice.inter_state = $("#interstate").is(":checked") ? "y" : "n";
	result.invoice.cform = $("#cform").is(":checked") ? "y" : "n";
	result.invoice.sub_total = $("#base-total").text();
	result.invoice.additional = $("#addnl-charges input").val();
	result.invoice.round_off_disc = $("#roundoff input").val();
	result.invoice.final_total = $("#grand-total").text();
	result.invoice.payment_mode = $("#payment-type-select").val();

	// specifies the action on the invoice , save , update or reject the invocie
	result.invoice.action = action;

	/** customer data * */
	result.customer = new Object();
	// Get the customer id, if a seletion was made from typeahead suggestions.
	if ($("#cust-name").data("customer") != undefined) {
		var customer_info = $("#cust-name").data("customer");
		result.customer.cust_id = customer_info.id;
		result.customer.name = customer_info.name;
		result.customer.phone = customer_info.phone1;
	} else {
		result.customer.cust_id = -1;
		result.customer.name = $("#cust-name").val();
		result.customer.phone = $("#cust-phone").val();
	}

	/** items listing * */
	result.items = [];
	$("#item-details tbody tr").each(function() {

		// skipping empty rows
		if ($(this).data("item-info")) {

			var item = new Object();

			item.item_id = $(this).data("item-info").id;
			item.name = $(this).data("item-info").name;
			item.qty = $(this).find("td.qty input").val();
			item.rate = $(this).data("item-info").rate;
			item.discount = $(this).find("td.disc-val").text();
			item.amount = $(this).find("td.amount").text();
			item.dsc_p = $(this).data("item-info").discnt_percent;
			item.category = $(this).data("item-info").catid;
			if ($("#cform").is(":checked")) {
				item.vat_p = 2.00 ; // if cform is checked it means its a 2% CST inter state Sale
			} else {
			item.vat_p = $(this).data("item-info").vat;
			}
			result.items.push(item);
		}
	});

	return result;
}

/**
 * Prepares a JSON object consisting of all user entered values for creating a
 * new purchase txn.
 */
function preparePurchaseJson(action) {
	var result = new Object();

	/** Overall invoice data * */
	result.invoice = new Object();
	result.invoice.invoice_no = $("#invoice-no1").val();
	result.invoice.pur_inv_no = $("#pur-invoice-no").val();
	result.invoice.invoice_date = convert2outputDt($("#date").text());
	result.invoice.inter_state = $("#interstate").is(":checked") ? "y" : "n";
	result.invoice.cform = $("#cform").is(":checked") ? "y" : "n";
	result.invoice.sub_total = $("#base-total").text();
	result.invoice.additional = $("#addnl-charges input").val();
	result.invoice.round_off_disc = $("#roundoff input").val();
	result.invoice.final_total = $("#grand-total").text();
	result.invoice.payment_mode = $("#payment-type").val();

	result.invoice.pur_inv_date = convert2outputDt1($("#pur-invoice-date")
			.val());
	result.invoice.goods_received_date = convert2outputDt1($("#receipt-date")
			.val());

	// specifies the action on the invoice , save , update or reject the invocie
	result.invoice.action = action;

	/** customer data * */
	result.customer = new Object();
	// Get the customer id, if a seletion was made from typeahead suggestions.
	if ($("#cust-name").data("customer") != undefined) {
		var customer_info = $("#cust-name").data("customer");
		result.customer.cust_id = customer_info.id;
		result.customer.name = customer_info.name;
		result.customer.phone = customer_info.phone1;
	} else {
		result.customer.cust_id = -1;
		result.customer.name = $("#cust-name").val();
		result.customer.phone = $("#cust-phone").val();
	}

	/** items listing * */
	result.items = [];
	$("#item-details tbody tr").each(function() {
		var item = new Object();

		item.item_id = $(this).data("item-info").id;
		item.qty = $(this).find("td.qty input").val();
		item.rate = $(this).data("item-info").rate;
		item.discount = $(this).find("td.disc-val").text();
		item.amount = $(this).find("td.amount").text();
		item.dsc_p = $(this).data("item-info").discnt_percent;
		if ($("#cform").is(":checked")) {
			item.vat_p = 2.00 ; // if cform is checked it means its a 2% CST inter state Sale
		} else {
		item.vat_p = $(this).data("item-info").vat;
		}
		item.category = $(this).data("item-info").catid;
		result.items.push(item);
	});

	return result;
}

function convert2outputDt(dt) {
	var date = dt.split(":")[1];
	date = date.split("/");
	return date[2].trim() + "-" + date[1].trim() + "-" + date[0].trim();
}

function convert2outputDt1(dt) {
	var date = dt.split("/");
	return date[2].trim() + "-" + date[1].trim() + "-" + date[0].trim();
}

/**
 * Submits the sale txn data to backend url through an ajax call.
 * 
 * @param {Object}
 *            data
 */
function submitSaleData(data) {

	$("#save-new-sale").text("Saving...");
	$("#save-new-sale").prop("disabled", true);
	$
			.ajax({
				url : "/eazytrack/EzTrackServlet?opcode=6",
				type : "POST",
				dataType : "json",
				timeout : 20000,
				data : {
					data : JSON.stringify(data)
				},
				success : function(data) {
					var result = confirm("Sale txn successfully completed. Do you want to print the invoice?");
					if (result == true) {
						var invoice_url = "/eazytrack/tmp/inv_"
								+ $("#invoice-no").val() + "_"
								+ $.cookie("brCode") + ".pdf";
						window.open(invoice_url, 'Invoice # '
								+ $("#invoice-no").val(), 'window settings');
					} else {

					}
				},
				error : function(xhr, status, text) {
					console.log("save status: " + status);
					alert("Unable to Save at this point of time. Retry later.");
					$("#save-new-sale").prop("disabled", false);
				},
				complete : function() {
					$("#save-new-sale").text("Save");
				}
			});
}

/**
 * Submits the purchase txn data to backend url through an ajax call.
 * 
 * @param {Object}
 *            data
 */
function submitPurchaseData(data) {

	$("#save-new-sale").text("Saving...");
	$("#save-new-sale").prop("disabled", true);
	$.ajax({
		url : "/eazytrack/EzTrackServlet?opcode=21",
		type : "POST",
		dataType : "json",
		timeout : 20000,
		data : {
			data : JSON.stringify(data)
		},
		success : function(data) {
			alert("Purchase txn successfully completed ");
		},
		error : function(xhr, status, text) {
			console.log("save status: " + status);
			alert("Unable to Save at this point of time. Retry later.");
			$("#save-new-sale").prop("disabled", false);
		},
		complete : function() {
			$("#save-new-sale").text("Save");
		}
	});
}

/**
 * Updates the tax value for cform 2%, non cfom original.
 * 
 * @param {Object}
 *            cform indicates if cform is true or not.
 */
function updateTax(cform) {

	var rows = $("#item-details tbody tr");
	if (cform) {
		for (var i = 0; i < rows.length; i++) {
			if ($(rows[i]).data("item-info") != undefined) {
				$(rows[i]).find("td.vat").text("2");
				updateRow($(rows[i]), false);
			}
		}
	} else {
		// copy the tax value from row's data.
		for (var i = 0; i < rows.length; i++) {
			if ($(rows[i]).data("item-info") != undefined) {
				var taxVal = $(rows[i]).data("item-info").vat;
				$(rows[i]).find("td.vat").text(taxVal);
				updateRow($(rows[i]), false);
			}
		}
	}

	updateTotals();
}

function addNewRow() {
	/**
	 *   If the last row in item table is already empty, don't add a new row.
	 */
	var rowFilled = $("table#item-details tbody tr:last").data("item-info");
	var newRow = $("table#item-details tbody tr:last").clone();
	$("table#item-details tbody").append(newRow);
	clearRow(newRow);
	bindTypeAhead(newRow.find("td.desc input"));

}
function cleartags(data){
	console.log("tags is"+data);
	$.ajax({
		url : "/eazytrack/EzTrackServlet?opcode=46",
		type : "POST",
		dataType : "json",
		timeout: 20000,
		data : {
			data : JSON.stringify(data)
		},
		success : function(data) {
			alert("tags are cleared successfully ");
		},
		error : function(xhr, status, text) {
			
			alert("Unable to clear tags at this point of time. Retry later.");
			
		},
		complete : function() {
			
		}
	});
	
}
