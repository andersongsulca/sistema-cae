/* ==================== INICIALIZACIÓN ==================== */
document.addEventListener('DOMContentLoaded', function() {
    console.log('✅ Sistema CAE - Cargado correctamente');
    
    // Inicializar tooltips de Bootstrap
    var tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    var tooltipList = tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
    
    // Inicializar funcionalidades
    initTableSearch();
    initDeleteConfirmations();
    initAutoHideAlerts();
    initActiveMenuHighlight();
    initFormValidation();
    initSmoothScroll();
    initInactivityTimeout(); // ✅ NUEVO: Inicializa el cierre por inactividad
    
    // Agregar clase active al enlace del menú actual
    highlightCurrentPage();
});

/* ==================== SEGURIDAD: CIERRE POR INACTIVIDAD ==================== */
let inactivityTimer;
const INACTIVITY_LIMIT = 60000; // 1 minuto en milisegundos (60,000 ms)

function resetInactivityTimer() {
    clearTimeout(inactivityTimer);
    inactivityTimer = setTimeout(() => {
        // Redirige al login con el parámetro que activa la alerta en tu HTML
        window.location.href = '/login?timeout=true';
    }, INACTIVITY_LIMIT);
}

function initInactivityTimeout() {
    // Solo activar el temporizador si NO estamos en la página de login
    // (Esto evita bucles de redirección infinita si el usuario está intentando loguearse)
    if (!window.location.pathname.includes('/login')) {
        resetInactivityTimer();
        
        // Eventos que reinician el contador de 1 minuto
        window.addEventListener('mousemove', resetInactivityTimer);
        window.addEventListener('keydown', resetInactivityTimer);
        window.addEventListener('click', resetInactivityTimer);
        window.addEventListener('scroll', resetInactivityTimer);
        window.addEventListener('touchstart', resetInactivityTimer); // Para móviles/tablets
    }
}

/* ==================== BÚSQUEDA EN TABLAS ==================== */
function initTableSearch() {
    // Buscar en todas las tablas que tengan un input de búsqueda
    const searchInputs = document.querySelectorAll('[id^="buscar"]');
    
    searchInputs.forEach(input => {
        input.addEventListener('keyup', function() {
            const searchTerm = this.value.toLowerCase();
            const tableId = this.id.replace('buscar', 'tabla');
            const table = document.getElementById(tableId) || 
                         this.closest('.panel-body, .card-body')?.querySelector('table');
            
            if (table) {
                const rows = table.querySelectorAll('tbody tr');
                let visibleCount = 0;
                
                rows.forEach(row => {
                    const text = row.textContent.toLowerCase();
                    const matches = text.includes(searchTerm);
                    row.style.display = matches ? '' : 'none';
                    if (matches) visibleCount++;
                });
                
                // Mostrar mensaje si no hay resultados
                const noResults = table.querySelector('.no-results');
                if (noResults) {
                    noResults.style.display = visibleCount === 0 ? '' : 'none';
                }
            }
        });
    });
}

/* ==================== CONFIRMACIÓN DE ELIMINACIÓN ==================== */
function initDeleteConfirmations() {
    const deleteForms = document.querySelectorAll('form[onsubmit*="confirm"]');
    
    deleteForms.forEach(form => {
        form.addEventListener('submit', function(e) {
            const message = this.getAttribute('onsubmit').match(/confirm\('(.+)'\)/);
            const confirmMessage = message ? message[1] : '¿Está seguro de realizar esta acción?';
            
            if (!confirm(confirmMessage)) {
                e.preventDefault();
            }
        });
    });
}

/* ==================== AUTO-HIDE ALERTS ==================== */
function initAutoHideAlerts() {
    const alerts = document.querySelectorAll('.alert:not(.alert-permanent), .cae-alert:not(.alert-permanent)');
    
    alerts.forEach(alert => {
        setTimeout(() => {
            if (alert.classList.contains('alert')) {
                const bsAlert = new bootstrap.Alert(alert);
                bsAlert.close();
            } else {
                alert.style.transition = 'opacity 0.5s ease';
                alert.style.opacity = '0';
                setTimeout(() => alert.remove(), 500);
            }
        }, 5000);
    });
}

/* ==================== RESALTAR MENÚ ACTIVO ==================== */
function initActiveMenuHighlight() {
    const currentPath = window.location.pathname;
    const navLinks = document.querySelectorAll('.cae-nav a');
    
    navLinks.forEach(link => {
        const href = link.getAttribute('href');
        if (href && currentPath.startsWith(href) && href !== '/') {
            link.classList.add('active');
        } else if (href === '/' && currentPath === '/') {
            link.classList.add('active');
        }
    });
}

function highlightCurrentPage() {
    const currentPath = window.location.pathname;
    const navLinks = document.querySelectorAll('.cae-nav a');
    
    navLinks.forEach(link => {
        link.classList.remove('active');
        const href = link.getAttribute('href');
        
        if (href === currentPath) {
            link.classList.add('active');
        } else if (href !== '/' && currentPath.startsWith(href)) {
            link.classList.add('active');
        }
    });
}

/* ==================== VALIDACIÓN DE FORMULARIOS ==================== */
function initFormValidation() {
    const forms = document.querySelectorAll('form');
    
    forms.forEach(form => {
        form.addEventListener('submit', function(e) {
            const requiredFields = form.querySelectorAll('[required]');
            let isValid = true;
            
            requiredFields.forEach(field => {
                if (!field.value.trim()) {
                    field.classList.add('is-invalid');
                    isValid = false;
                } else {
                    field.classList.remove('is-invalid');
                }
            });
            
            if (!isValid) {
                e.preventDefault();
                showNotification('Por favor complete todos los campos obligatorios', 'warning');
                
                // Scroll al primer campo inválido
                const firstInvalid = form.querySelector('.is-invalid');
                if (firstInvalid) {
                    firstInvalid.scrollIntoView({ behavior: 'smooth', block: 'center' });
                    firstInvalid.focus();
                }
            }
        });
    });
    
    // Limpiar error al escribir
    document.querySelectorAll('.form-control, .form-select').forEach(input => {
        input.addEventListener('input', function() {
            this.classList.remove('is-invalid');
        });
    });
}

function validateForm(formId) {
    const form = document.getElementById(formId);
    if (!form) return false;
    
    const inputs = form.querySelectorAll('input[required], select[required], textarea[required]');
    let isValid = true;
    
    inputs.forEach(input => {
        if (!input.value.trim()) {
            input.classList.add('is-invalid');
            isValid = false;
        } else {
            input.classList.remove('is-invalid');
        }
    });
    
    return isValid;
}

/* ==================== FORMATO DE MONEDA ==================== */
function formatCurrency(amount) {
    return new Intl.NumberFormat('es-PE', {
        style: 'currency',
        currency: 'PEN'
    }).format(amount);
}

/* ==================== FORMATO DE FECHA ==================== */
function formatDate(dateString) {
    const options = { year: 'numeric', month: '2-digit', day: '2-digit' };
    return new Date(dateString).toLocaleDateString('es-PE', options);
}

function formatDateTime(dateString) {
    const options = { 
        year: 'numeric', 
        month: '2-digit', 
        day: '2-digit',
        hour: '2-digit',
        minute: '2-digit'
    };
    return new Date(dateString).toLocaleDateString('es-PE', options);
}

/* ==================== NOTIFICACIONES ==================== */
function showNotification(message, type = 'info') {
    const notification = document.createElement('div');
    notification.className = `alert alert-${type} alert-dismissible fade show position-fixed`;
    notification.style.cssText = 'top: 20px; right: 20px; z-index: 9999; min-width: 300px; box-shadow: 0 4px 12px rgba(0,0,0,0.15);';
    notification.innerHTML = `
        <i class="bi bi-${type === 'success' ? 'check-circle' : type === 'danger' ? 'exclamation-circle' : type === 'warning' ? 'exclamation-triangle' : 'info-circle'} me-2"></i>
        ${message}
        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
    `;
    
    document.body.appendChild(notification);
    
    setTimeout(() => {
        notification.style.transition = 'opacity 0.5s ease';
        notification.style.opacity = '0';
        setTimeout(() => notification.remove(), 500);
    }, 5000);
}

/* ==================== EXPORTAR TABLA A CSV ==================== */
function exportTableToCSV(tableId, filename = 'export.csv') {
    const table = document.getElementById(tableId);
    if (!table) return;
    
    let csv = [];
    const rows = table.querySelectorAll('tr');
    
    rows.forEach(row => {
        const cols = row.querySelectorAll('td, th');
        let csvRow = [];
        
        cols.forEach(col => {
            csvRow.push('"' + col.textContent.trim().replace(/"/g, '""') + '"');
        });
        
        csv.push(csvRow.join(','));
    });
    
    downloadCSV(csv.join('\n'), filename);
}

function downloadCSV(csv, filename) {
    const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.setAttribute('hidden', '');
    a.setAttribute('href', url);
    a.setAttribute('download', filename);
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    window.URL.revokeObjectURL(url);
}

/* ==================== EXPORTAR A EXCEL ==================== */
function exportTableToExcel(tableId, filename = 'export.xlsx') {
    const table = document.getElementById(tableId);
    if (!table) return;
    
    let html = table.outerHTML;
    html = html.replace(/<a[^>]*>|<\/a>/gi, '').replace(/<input[^>]*>|<\/input>/gi, '');
    
    const blob = new Blob(['\ufeff', html], {
        type: 'application/vnd.ms-excel'
    });
    
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = filename;
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
    window.URL.revokeObjectURL(url);
}

/* ==================== FILTRADO AVANZADO ==================== */
function filterTable(tableId, columnIndex, searchTerm) {
    const table = document.getElementById(tableId);
    if (!table) return;
    
    const rows = table.querySelectorAll('tbody tr');
    
    rows.forEach(row => {
        const cell = row.querySelectorAll('td')[columnIndex];
        if (cell) {
            const text = cell.textContent.toLowerCase();
            row.style.display = text.includes(searchTerm.toLowerCase()) ? '' : 'none';
        }
    });
}

/* ==================== MODALES ==================== */
function openModal(modalId) {
    const modal = new bootstrap.Modal(document.getElementById(modalId));
    modal.show();
}

function closeModal(modalId) {
    const modal = bootstrap.Modal.getInstance(document.getElementById(modalId));
    if (modal) {
        modal.hide();
    }
}

/* ==================== UTILIDADES ==================== */
function debounce(func, wait) {
    let timeout;
    return function executedFunction(...args) {
        const later = () => {
            clearTimeout(timeout);
            func(...args);
        };
        clearTimeout(timeout);
        timeout = setTimeout(later, wait);
    };
}

function copyToClipboard(text) {
    navigator.clipboard.writeText(text).then(() => {
        showNotification('Copiado al portapapeles', 'success');
    }).catch(() => {
        showNotification('Error al copiar', 'danger');
    });
}

function smoothScroll(elementId) {
    const element = document.getElementById(elementId);
    if (element) {
        element.scrollIntoView({ behavior: 'smooth', block: 'start' });
    }
}

function initSmoothScroll() {
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            const href = this.getAttribute('href');
            if (href !== '#' && href.length > 1) {
                e.preventDefault();
                const target = document.querySelector(href);
                if (target) {
                    target.scrollIntoView({ behavior: 'smooth' });
                }
            }
        });
    });
}

/* ==================== PRINT ==================== */
function printElement(elementId) {
    const element = document.getElementById(elementId);
    if (element) {
        const printWindow = window.open('', '', 'height=600,width=800');
        printWindow.document.write('<html><head><title>Imprimir</title>');
        printWindow.document.write('<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">');
        printWindow.document.write('<link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css" rel="stylesheet">');
        printWindow.document.write('<style>body { padding: 20px; }</style>');
        printWindow.document.write('</head><body>');
        printWindow.document.write(element.innerHTML);
        printWindow.document.write('</body></html>');
        printWindow.document.close();
        
        setTimeout(() => {
            printWindow.print();
            printWindow.close();
        }, 500);
    }
}

/* ==================== VALIDACIÓN DE STOCK ==================== */
function validarStock(select) {
    const fila = select.closest('tr');
    const cantidadInput = fila.querySelector('.cantidad-input, input[type="number"]');
    const stockError = fila.querySelector('.stock-error');
    
    if (!cantidadInput || !select) return true;
    
    const optionSelected = select.options[select.selectedIndex];
    const stockDisponible = parseFloat(optionSelected.dataset.stock) || 0;
    const cantidadSolicitada = parseFloat(cantidadInput.value) || 0;
    
    if (cantidadSolicitada > stockDisponible) {
        if (stockError) {
            stockError.style.display = 'block';
            stockError.textContent = `Stock disponible: ${stockDisponible}`;
        }
        cantidadInput.classList.add('is-invalid');
        return false;
    } else {
        if (stockError) {
            stockError.style.display = 'none';
        }
        cantidadInput.classList.remove('is-invalid');
        return true;
    }
}

/* ==================== CÁLCULOS AUTOMÁTICOS ==================== */
function calcularTotal() {
    const cantidades = document.querySelectorAll('.cantidad-input, input[name="cantidades"]');
    let total = 0;
    
    cantidades.forEach(input => {
        total += parseFloat(input.value) || 0;
    });
    
    return total;
}

/* ==================== MANEJO DE ARCHIVOS ==================== */
function previewImage(input, previewId) {
    const preview = document.getElementById(previewId);
    if (input.files && input.files[0]) {
        const reader = new FileReader();
        reader.onload = function(e) {
            preview.src = e.target.result;
            preview.style.display = 'block';
        };
        reader.readAsDataURL(input.files[0]);
    }
}

/* ==================== CONFIRMACIONES PERSONALIZADAS ==================== */
function confirmAction(message, callback) {
    if (confirm(message)) {
        callback();
    }
}

/* ==================== LOADING STATES ==================== */
function showLoading(buttonId) {
    const button = document.getElementById(buttonId);
    if (button) {
        button.disabled = true;
        button.dataset.originalText = button.innerHTML;
        button.innerHTML = '<i class="bi bi-arrow-repeat spin"></i> Procesando...';
    }
}

function hideLoading(buttonId) {
    const button = document.getElementById(buttonId);
    if (button) {
        button.disabled = false;
        button.innerHTML = button.dataset.originalText || 'Enviar';
    }
}

/* ==================== ANIMACIONES ==================== */
function fadeIn(element) {
    element.style.opacity = '0';
    element.style.display = 'block';
    element.style.transition = 'opacity 0.3s ease';
    setTimeout(() => {
        element.style.opacity = '1';
    }, 10);
}

function fadeOut(element, callback) {
    element.style.transition = 'opacity 0.3s ease';
    element.style.opacity = '0';
    setTimeout(() => {
        element.style.display = 'none';
        if (callback) callback();
    }, 300);
}

/* ==================== KEYBOARD SHORTCUTS ==================== */
document.addEventListener('keydown', function(e) {
    // Ctrl + S para guardar (prevenir default y mostrar mensaje)
    if (e.ctrlKey && e.key === 's') {
        e.preventDefault();
        showNotification('Use el botón de guardar del formulario', 'info');
    }
    
    // Escape para cerrar modales
    if (e.key === 'Escape') {
        const modals = document.querySelectorAll('.modal.show');
        modals.forEach(modal => {
            const bsModal = bootstrap.Modal.getInstance(modal);
            if (bsModal) bsModal.hide();
        });
    }
});

/* ==================== RESPONSIVE TABLE HELPER ==================== */
function makeTableResponsive() {
    const tables = document.querySelectorAll('.table');
    tables.forEach(table => {
        if (!table.parentElement.classList.contains('table-responsive')) {
            const wrapper = document.createElement('div');
            wrapper.className = 'table-responsive';
            table.parentElement.insertBefore(wrapper, table);
            wrapper.appendChild(table);
        }
    });
}

// Ejecutar al cargar
document.addEventListener('DOMContentLoaded', makeTableResponsive);

console.log('✅ Todas las funcionalidades del Sistema CAE cargadas correctamente');