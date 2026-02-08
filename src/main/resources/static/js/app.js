import "./eventListeners.js"
import {initializeSizes} from "./productDetails.js";
import {toggleCard} from "./order.js";
import "./passwordToggler.js"

export let isCartEmpty = window.cartEmpty
export const variants = window.variants
export let selectedVariant = null
export const orderDtos = window.orderDtos

export function setSelectedVariant(value){
    selectedVariant = value;
}


displayCartSignIfCartNotEmpty()
initializeSizes();
toggleCard(true);


function displayCartSignIfCartNotEmpty() {
    if (isCartEmpty === false) {
        document.getElementById("cart-sign").style.display = "inline-block"
    }
}

export function animate(isOk) {
    if (isOk) {
        isCartEmpty = false
        displayCartSignIfCartNotEmpty()
        anime({
            targets: '#cart-sign',
            scale: [1.1, 1.2, 1.3, 1.4, 1.5, 1.4, 1.3, 1.2, 1.1, 1],
            duration: 700,
            easing: 'easeOutQuad'
        });
    }
}

export async function sendRequest(quantityId) {
    let variantId = selectedVariant.id
    let quantity = parseInt(document.getElementById(quantityId).value)
    let response = await fetch(`/cart/addToCart/${variantId}/${quantity}`)
    return response.ok
}