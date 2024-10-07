const toggleSidebar = () => {
    if ($('.sidebar').is(":visible")) {
        $(".sidebar").css("display", "none");
        $(".content").css("margin-left", "0%");
    } else {
        $(".sidebar").css("display", "block");
        $(".content").css("margin-left", "20%");
    }
};

/*const search=() =>{

   // console.log("Searching");

let query=$("#search-input").val();
if(query=="")
{
    $(".search-result").hide();

}
else{

    //sending request to server
    
    let url=`http://localhost:5300/search/${query}`;
    fetch(url).then((response)=>
    {
return response.json();
    }).then((data)=>{

console.log(data);
let text=`<div class='list-group'>`;

//traverse data
data.forEach((contact) => {
    text+=`<a href='#' class='list-group-item list-group-action'${contact.name}</a>`;
    
});

text+=`</div>`
$(".search-result").html(text);
$(".search-result").show();

    })


   
}

};*/
const search = () => {
    // Get the search query from the input field
    let query = $("#search-input").val();

    if (query == "") {
        // Hide the search results if the query is empty
        $(".search-result").hide();
    } 
	
	else {
        // Construct the URL with the query
        let url = `http://localhost:5300/search/${query}`;

        // Send a GET request to the server
        fetch(url)
            .then((response) => response.json()) // Parse the JSON response
            .then((data) => {
                console.log(data); // Log the data to the console

                // Initialize the HTML string to display results
                let text = `<div class='list-group'>`;

                // Iterate through the data and create list items for each contact
                data.forEach((contact) => {
                    text += `<a href='/user/contact/${contact.cId}' class='list-group-item list-group-item-action'>${contact.name}</a>`;
                });

                // Close the div tag
                text += `</div>`;

                // Insert the results into the HTML and show the search results
                $(".search-result").html(text);
                $(".search-result").show();
            })
            .catch((error) => {
                console.error('Error:', error); // Handle any errors
            });
    }
};

//first request to server to create order

const paymentStart=()=>{
	
	console.log("payment started")
	let amount=$("#paymentfield").val();
	console.log(amount)
	if(amount==null||amount==''||isNaN(amount))
		{
			swal("Failed!","Invalid Amount","error");
			return;
		}
		
		//send request to server 
		//we will use jquery ajax to send request to create order
		//use jquery min.js
		
		$.ajax({
		        url: '/user/create_order',  // Keep the URL clean
		       
		          
		        data: JSON.stringify({amount: amount,info: 'order_request'}),
				contentType: 'application/json',
				type: 'POST',
		        dataType: 'json',  // Expect JSON response from server
		        success: function(response) {
		            console.log(response);
		            // Handle success
					
					if(response.status=="created")
						{
							let options = {
							    key: 'rzp_test_jEshvXbr5mBElc',  // Make sure this is your actual Razorpay key
							    amount: response.amount,          // Amount is in the smallest currency unit (e.g., paise for INR)
							    currency: 'INR',
							    name: "Smart Contact Manager",    // Your business name
							    description: "donation",         // Brief description of the purpose of the payment
							    image: 'https://i.ibb.co/WcMf4mH/your-image.jpg',  // Direct link to your image
							    order_id: response.id,            // The order ID returned by your server
							    handler: function (response) {
							       console.log(response.razorpay_payment_id);
							       console.log(response.razorpay_order_id);
							       console.log(response.razorpay_signature);
								
								   updatePaymentOnServer(response.razorpay_payment_id,response.razorpay_order_id,"paid");
								  
							    },
							    prefill: {
							        name: "",  // Prefilled name
							        email: "",  // Prefilled email
							        contact: ""  // Prefilled contact number
							    },
							    notes: {
							        address: "Hello From Himanshu"  // Any additional notes
							    },
							    theme: {
							        color: "#3399cc"  // Color of the payment modal theme
							    }
							};

							let rzp=new Razorpay(options);
							
							rzp.on('payment.failed', function (response){
							console.log(response.error.code);
							console.log(response.error.description);
							console.log(response.error.source);
							console.log(response.error.step);
							console.log(response.error.reason);
							console.log(response.error.metadata.order_id);
						    console.log(response.error.metadata.payment_id);
							swal("Failed!","OOPS! Payment Failed!!","error");
							});
							
							rzp.open();
						}
		        },
		        error: function(error) {
		            console.log(error);
		            alert("Something went wrong !!");
		        }
		    });
		};

//updating paymrnt id

function updatePaymentOnServer(payment_id,order_id,status)
{
	
	$.ajax({
		
		
		
		            url: '/user/update_order',  
			        data:JSON.stringify({
						payment_id: payment_id,
						order_id:order_id,
						status:status
					}),
					contentType: 'application/json',
					type: 'POST',
			        dataType: 'json', 
					success:function(response)
					{
						
						swal("Good Job!","Payment Successfull!!","success");
					},
					error:function(error)
					{
						swal("failed!",
						"Your Payment Successfull,but we did not get on server,will contact you",
						"error");
					}
					
	});
	
	
}






