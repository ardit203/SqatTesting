import {addToCartFromProducts, checkBeforeRequest, closeOverlay} from "./products.js";
import {addToCart, changeSize} from "./productDetails.js";
import {handleChange, check} from "./cart.js";
import {previewOrder} from "./profile.js";
import {toggleCard} from "./order.js";

//products
document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll(".add-to-cart-from-products-btn").forEach(btn => {
        btn.addEventListener("click", () => {
            const productId = Number(btn.dataset.productId);
            addToCartFromProducts(productId);
        });
    });
});
document.addEventListener("click", (e) => {
    const btn = e.target.closest(".check-before-request-btn");
    if (!btn) return;

    checkBeforeRequest(btn);
});
document.addEventListener("click", (e) => {
    const btn = e.target.closest(".close-products-overlay");
    if (!btn) return;

    closeOverlay();
});

//product-details
document.addEventListener("DOMContentLoaded", () => {
    const btn = document.getElementsByClassName("add-to-cart-btn")[0];
    if (!btn) return;

    btn.addEventListener("click", (e) => {
        addToCart();
    });
});
document.addEventListener("click", (e) => {
    const btn = e.target.closest(".size-btn");
    if (!btn) return;

    changeSize(btn)
});


//cart
document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll(".handle-change").forEach(btn => {
        btn.addEventListener("input", () => {
            const cartVariantId = Number(btn.dataset.cartVariantId);
            const price = Number(btn.dataset.price);
            handleChange(cartVariantId, price);
        });
    });
});
document.addEventListener("DOMContentLoaded", () => {
    const btn = document.getElementById("check-for-stocks")
    if (!btn) return;

    btn.addEventListener("click", (e) => {
        check();
    });
});

//profile
document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll(".preview-order").forEach(btn => {
        btn.addEventListener("click", () => {
            const orderId = Number(btn.dataset.orderId);
            previewOrder(orderId);
        });
    });
});

//order
document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll(".toggle-card").forEach(btn => {
        btn.addEventListener("click", () => {
            const toggle = btn.dataset.toggle === "true";
            toggleCard(toggle)
        });
    });
});