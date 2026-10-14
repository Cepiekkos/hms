const http = require('http');
const fs = require('fs');
const path = require('path');

const PORT = 8085;
const BASE_DIR = __dirname;

const MIME_TYPES = {
    '.html': 'text/html',
    '.css': 'text/css',
    '.js': 'application/javascript',
    '.json': 'application/json',
    '.png': 'image/png',
    '.jpg': 'image/jpeg',
    '.svg': 'image/svg+xml'
};

const server = http.createServer((req, res) => {
    console.log(`[${new Date().toISOString()}] ${req.method} ${req.url}`);

    // Emulate AEM Sling Servlet POST /bin/healthcare/book-appointment
    if (req.url === '/bin/healthcare/book-appointment' && req.method === 'POST') {
        let body = '';
        req.on('data', chunk => { body += chunk; });
        req.on('end', () => {
            const code = 'APT-FHIR-' + Math.floor(100000 + Math.random() * 900000);
            res.writeHead(200, { 'Content-Type': 'application/json' });
            res.end(JSON.stringify({
                status: 'CONFIRMED',
                confirmationCode: code,
                timestamp: new Date().toISOString(),
                fhirResource: 'Appointment/APT-' + code
            }));
        });
        return;
    }

    // Emulate AEM Sling Servlet GET /bin/healthcare/patient-records
    if (req.url.startsWith('/bin/healthcare/patient-records') && req.method === 'GET') {
        res.writeHead(200, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify({
            patientId: 'PAT-90821',
            fullName: 'Eleanor Vance',
            dob: '1988-04-12',
            gender: 'Female',
            bloodGroup: 'O+',
            primaryPhysician: 'Dr. Sarah Jenkins (Cardiology)'
        }));
        return;
    }

    // Emulate AEM Sling Servlet GET /bin/healthcare/export-ehr-pdf
    if (req.url.startsWith('/bin/healthcare/export-ehr-pdf')) {
        const urlParams = new URL(req.url, `http://${req.headers.host}`);
        const testName = urlParams.searchParams.get('test') || 'Diagnostic Report';
        const patientId = urlParams.searchParams.get('patientId') || 'PAT-90821';
        const filename = `EHR_${testName.replace(/[^a-zA-Z0-9]/g, '_')}_${patientId}.pdf`;

        // Generate a standard PDF document
        const stream = `BT
/F1 18 Tf
50 720 Td
(AURAHEALTH EMR - OFFICIAL CLINICAL DIAGNOSTIC REPORT) Tj
/F1 12 Tf
0 -40 Td
(Patient Name: Eleanor Vance  |  Patient ID: ${patientId}) Tj
0 -25 Td
(Date of Service: 2026-09-15  |  Verified By: Dr. Sarah Jenkins, MD) Tj
0 -30 Td
(Diagnostic Panel: ${testName}) Tj
0 -25 Td
(Clinical Findings: All metabolic markers within reference ranges. No acute abnormalities.) Tj
0 -25 Td
(Status: Verified & Signed via HL7 FHIR R4 DiagnosticReport Resource) Tj
ET`;
        const pdf = `%PDF-1.4
1 0 obj
<< /Type /Catalog /Pages 2 0 R >>
endobj
2 0 obj
<< /Type /Pages /Kids [3 0 R] /Count 1 >>
endobj
3 0 obj
<< /Type /Page /Parent 2 0 R /Resources << /Font << /F1 4 0 R >> >> /MediaBox [0 0 612 792] /Contents 5 0 R >>
endobj
4 0 obj
<< /Type /Font /Subtype /Type1 /BaseFont /Helvetica >>
endobj
5 0 obj
<< /Length ${Buffer.byteLength(stream)} >>
stream
${stream}
endstream
endobj
xref
0 6
0000000000 65535 f 
0000000009 00000 n 
0000000058 00000 n 
0000000115 00000 n 
0000000228 00000 n 
0000000300 00000 n 
trailer
<< /Size 6 /Root 1 0 R >>
startxref
${400 + Buffer.byteLength(stream)}
%%EOF`;

        res.writeHead(200, {
            'Content-Type': 'application/pdf',
            'Content-Disposition': `attachment; filename="${filename}"`
        });
        res.end(pdf);
        return;
    }

    // Static file routing
    let reqPath = req.url.split('?')[0];
    if (reqPath === '/' || reqPath === '/healthcare') {
        reqPath = '/login.html';
    }

    const filePath = path.join(BASE_DIR, reqPath);

    fs.readFile(filePath, (err, data) => {
        if (err) {
            res.writeHead(404, { 'Content-Type': 'text/plain' });
            res.end('404 Not Found: ' + req.url);
            return;
        }

        const ext = path.extname(filePath).toLowerCase();
        const contentType = MIME_TYPES[ext] || 'application/octet-stream';
        res.writeHead(200, { 'Content-Type': contentType });
        res.end(data);
    });
});

server.listen(PORT, () => {
    console.log(`Healthcare Management System server is active at http://localhost:${PORT}`);
});
