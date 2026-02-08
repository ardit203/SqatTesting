export async function check() {
    let list = [];
    let divs = document.getElementsByClassName("cart-variant");

    for (let i = 0; i < divs.length; i++) {
        const id = divs[i].dataset.cartVariantId;
        list.push({
            cartVariantId: Number(id),
            cartId: Number(divs[i].dataset.cartId),
            variantId: Number(divs[i].dataset.variantId),
            quantity: Number(document.getElementById(`${id}-quantity`).value)
        });
    }

    const res = await fetch("/cart/check", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",

        },
        credentials: "same-origin",
        body: JSON.stringify(list)
    });

    const data = await res.json();

    if (data.redirect) window.location.href = data.redirect;
    if (data.error){
        console.log(data.error)
        showErrorOverlay(data.error, "Cart error");
    }
}

function showErrorOverlay(message, title = "Error") {

    let overlay = document.getElementById("errorOverlay");
    if (overlay == null) {
        overlay = document.createElement("div");
        overlay.id = "errorOverlay";
        overlay.className = "err-overlay";
        overlay.innerHTML = `
      <div class="err-card" role="alert" aria-live="assertive">
        <div class="err-icon" aria-hidden="true">!</div>

        <div class="err-content">
          <div class="err-title" id="errTitle"></div>
          <div class="err-msg" id="errMsg"></div>
        </div>

        <button type="button" class="err-close" aria-label="Close">×</button>
      </div>
    `;
        document.body.appendChild(overlay);

        // close actions
        overlay.addEventListener("click", (e) => {
            if (e.target === overlay) hideErrorOverlay();
        });
        overlay.querySelector(".err-close").addEventListener("click", hideErrorOverlay);
        document.addEventListener("keydown", (e) => {
            if (e.key === "Escape") hideErrorOverlay();
        });
    }

    overlay.querySelector("#errTitle").textContent = title;
    overlay.querySelector("#errMsg").textContent = message;

    overlay.classList.add("is-open");
}

function hideErrorOverlay() {
    const overlay = document.getElementById("errorOverlay");
    if (overlay) overlay.classList.remove("is-open");
}

export async function handleChange(cartVariantId, price){
    let total = document.getElementById("total");//with dollar sign
    let subtotal = document.getElementById("subtotal")//with dollar sign
    let prices = document.getElementsByClassName("price"); //with dollar sign
    let quantities = document.getElementsByClassName("quantity");
    let productTotalElem = document.getElementById(`${cartVariantId}-total`)
    let quantityElem = document.getElementById(`${cartVariantId}-quantity`)
    let quantity = parseInt(quantityElem.value)

    let response = await fetch(`/cart/updateCart/${cartVariantId}/${quantity}`)

    if(!response.ok){
        quantityElem.value = (quantity - 1)
        return;
    }

    let sum = 0;

    for (let i = 0; i < prices.length; i++) {
        let currPrice = Number(prices[i].textContent.split("$")[1])
        let qty = parseInt(quantities[i].value)
        sum += (currPrice * qty)
    }


    productTotalElem.textContent = moneyUSD(quantity * price)
    subtotal.textContent = moneyUSD(sum)
    total.textContent = moneyUSD(sum + 3)
}

function moneyUSD(value) {
    return new Intl.NumberFormat("en-US", {
        style: "currency",
        currency: "USD",
        minimumFractionDigits: 2,
        maximumFractionDigits: 2
    }).format(Number(value));
}
