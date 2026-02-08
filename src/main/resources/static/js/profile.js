import {orderDtos} from "./app.js";

export function previewOrder(orderId) {
    const order = orderDtos.find(o => String(o.orderId) === String(orderId));
    if (!order) return;

    const itemsHtml = (order.products || []).map(p => `
            <div class="ord-item d-flex align-items-center justify-content-between">
              <div class="ord-thumb">
              <a href="${p.image}" target="_blank">
                    <img src="${p.image}" alt="${p.productName}" loading="lazy">
               </a>
              </div>
        
              <div class="ord-info">
        
              <div class="ord-row ord-name">${p.productName}</div>
                <div class="ord-row ord-sub">${p.department} • ${p.categoryName}</div>
        
                <div class="ord-grid">
                  <div class="ord-pill">Size: <span>${p.size}</span></div>
                  <div class="ord-pill">Quantity: <span>${p.quantity}</span></div>
                </div>
        
        
              </div>
            </div>
  `).join("");

    // If already open, just update content
    let overlay = document.getElementById("orderPreviewOverlay");
    if (!overlay) {
        overlay = document.createElement("div");
        overlay.id = "orderPreviewOverlay";
        overlay.className = "ord-backdrop";
        overlay.innerHTML = `
              <div class="ord-modal" role="dialog" aria-modal="true" aria-labelledby="ordTitle">
                <div class="ord-head">
                  <div>
                    <div class="ord-title" id="ordTitle"></div>
                    <div class="ord-muted" id="ordSubtitle"></div>
                  </div>
        
                  <button type="button" class="ord-x" aria-label="Close">×</button>
                </div>
        
                <div class="ord-body">
                  <div class="ord-list" id="ordList"></div>
                </div>
              </div>
    `;
        document.body.appendChild(overlay);

        // close on outside click
        overlay.addEventListener("click", (e) => {
            if (e.target === overlay) closePreviewOrder();
        });
        // close on X
        overlay.querySelector(".ord-x").addEventListener("click", closePreviewOrder);
        // close on ESC
        document.addEventListener("keydown", escCloseHandler);
    }

    overlay.querySelector("#ordTitle").textContent = `Order #${orderId}`;
    overlay.querySelector("#ordSubtitle").textContent = `${(order.products || []).length} item(s)`;
    overlay.querySelector("#ordList").innerHTML = itemsHtml || `<div class="ord-empty">No products in this order.</div>`;

    overlay.classList.add("ord-show");
    document.body.classList.add("ord-lock");
}

function escCloseHandler(e) {
    if (e.key === "Escape") closePreviewOrder();
}

function closePreviewOrder() {
    const overlay = document.getElementById("orderPreviewOverlay");
    if (overlay) overlay.remove();
    document.body.classList.remove("ord-lock");
    document.removeEventListener("keydown", escCloseHandler);
}

// document.addEventListener("DOMContentLoaded", () => {
//     document.querySelectorAll(".preview-order").forEach(btn => {
//         btn.addEventListener("click", () => {
//             const orderId = Number(btn.dataset.orderId);
//             previewOrder(orderId);
//         });
//     });
// });