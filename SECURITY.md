# Security Policy

## Supported Versions
The library supports all publicly released versions **except `1.1.1`**, which is explicitly excluded from security maintenance.

| Version | Status |
|---------|--------|
| 1.1.1 | Unsupported (excluded) |
| All other released versions | Supported |

Refer to the releases page for the authoritative list:
https://github.com/snowykte0426/peanut-butter/releases

## Reporting a Vulnerability
Please do **not** open a public issue for suspected security problems. Instead:

1. Create a draft advisory via GitHub: Repository > Security > Advisories.
2. Include:
   - Summary (short description)
   - Affected versions (confirm exclusion of 1.1.1 only)
   - Impact / risk description
   - Reproduction steps (minimal, deterministic)
   - Proof of Concept (if possible)
   - Temporary mitigations / workarounds (if any)
3. (Optional) Provide supporting details:
   - Preliminary CVSS vector / score
   - Attack prerequisites (auth required? specific config?)
   - Impact classification (privilege escalation, info disclosure, RCE, DoS, integrity)
4. Submit and wait for maintainer response (target initial acknowledgement: 3–5 business days).

If GitHub Advisory cannot be used in your context, you may prepare the same information and (once an alternate private channel is documented) send it there. Public issue trackers are discouraged for initial disclosure.

### Email Template (If Needed)
```
Subject: [Security] <concise summary>
Affected Versions: ... (all releases except 1.1.1 are normally supported)
Environment: (JDK, OS, build tooling)
Description:
Reproduction Steps:
Expected vs Actual:
PoC:
Mitigations:
Additional Notes:
```

## Triage & Response Targets
| Phase | Objective | Target Time |
|-------|-----------|-------------|
| Initial Review | Validate reproducibility, classify severity | 3–5 business days |
| Patch Plan | Determine remediation approach (High/Critical) | Within 10 business days |
| Fix & Test | Implement + regression tests | Depends on complexity |
| Release & Advisory | Publish fixed version + advisory | With patch release |

## Disclosure Principles
- Avoid premature public release of exploit details before a patch is available.
- Publish: affected versions, risk summary, remediation steps, and migration guidance when releasing the fix.
- Credit researchers if they request attribution; respect anonymity if preferred.

## Non-Security Issues
Use standard issue templates for bugs, features, documentation, or performance discussions.

## Responsible Disclosure
Good faith research is appreciated. Keep testing within reasonable, non‑destructive bounds. Do not intentionally degrade service or exfiltrate data. Provide sufficient detail for reproducibility without sharing unnecessary sensitive information.

## Contact
Primary channel: GitHub Security Advisory workflow. Additional private contact methods may be added later in the README/security page.

Thank you for helping keep the project secure.
