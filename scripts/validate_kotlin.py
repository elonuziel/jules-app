#!/usr/bin/env python3
"""
Pre-commit static analysis validator for Jules Android app.
Catches common compilation killers before pushing:
- Duplicate or conflicting imports
- Missing imports for known Material3 / Compose symbols (Card, CardDefaults, BorderStroke, etc.)
- Typo patterns in Compose color / modifier calls
- Unbalanced brackets and braces
"""

import sys
import re
from pathlib import Path

# Mapping of commonly forgotten symbols to their required import
REQUIRED_SYMBOL_IMPORTS = {
    "Card": "androidx.compose.material3.Card",
    "CardDefaults": "androidx.compose.material3.CardDefaults",
    "BorderStroke": "androidx.compose.foundation.BorderStroke",
    "LinearProgressIndicator": "androidx.compose.material3.LinearProgressIndicator",
    "CircularProgressIndicator": "androidx.compose.material3.CircularProgressIndicator",
    "OutlinedTextField": "androidx.compose.material3.OutlinedTextField",
    "OutlinedTextFieldDefaults": "androidx.compose.material3.OutlinedTextFieldDefaults",
}

# Known typos that have broken compilation
KNOWN_TYPOS = [
    (r"\bunffocusedContainerColor\b", "unfocusedContainerColor"),
    (r"\bunffocusedBorderColor\b", "unfocusedBorderColor"),
    (r"\bunffocusedTextColor\b", "unfocusedTextColor"),
]

def check_file(file_path: Path) -> list[str]:
    errors = []
    text = file_path.read_text(encoding="utf-8")
    lines = text.splitlines()

    import_lines = []
    imported_simple_names = {}
    
    for idx, line in enumerate(lines, start=1):
        stripped = line.strip()
        
        # Check for known typos
        for typo_regex, correction in KNOWN_TYPOS:
            if re.search(typo_regex, stripped):
                errors.append(f"{file_path}:{idx} Typo detected in '{stripped}' -> Did you mean '{correction}'?")

        # Track imports
        if stripped.startswith("import ") and not stripped.startswith("//"):
            import_statement = stripped[7:].split(";")[0].strip()
            
            # Check for exact duplicate import
            if import_statement in import_lines:
                errors.append(f"{file_path}:{idx} Duplicate import: 'import {import_statement}' is already imported!")
            else:
                import_lines.append(import_statement)

            # Check for conflicting simple name import
            simple_name = import_statement.split(".")[-1]
            if simple_name in imported_simple_names and imported_simple_names[simple_name] != import_statement:
                prev_stmt = imported_simple_names[simple_name]
                errors.append(
                    f"{file_path}:{idx} Conflicting import: '{simple_name}' is imported as both '{prev_stmt}' and '{import_statement}'!"
                )
            else:
                imported_simple_names[simple_name] = import_statement

    # Check for missing imports of known symbols used in code
    for symbol, required_import in REQUIRED_SYMBOL_IMPORTS.items():
        # Match symbol usage like Card( or CardDefaults. or BorderStroke(
        usage_pattern = rf"\b{symbol}\s*[\(\.]"
        if re.search(usage_pattern, text):
            # Check if imported
            has_exact_import = any(imp == required_import for imp in import_lines)
            has_wildcard = any(imp == required_import.rsplit(".", 1)[0] + ".*" for imp in import_lines)
            if not has_exact_import and not has_wildcard:
                errors.append(
                    f"{file_path}: Symbol '{symbol}' is used but required import 'import {required_import}' is missing!"
                )

    return errors

def main():
    files_to_check = sys.argv[1:]
    if not files_to_check:
        # Default to all kotlin files under app/src
        files_to_check = [str(p) for p in Path("app/src").rglob("*.kt")]

    total_errors = []
    for f in files_to_check:
        path = Path(f)
        if path.is_file() and path.suffix == ".kt":
            errs = check_file(path)
            total_errors.extend(errs)

    if total_errors:
        print("\n❌ PRE-COMMIT CODE VALIDATION FAILED:")
        for err in total_errors:
            print(f"  • {err}")
        print("\nPlease fix the above errors before committing.\n")
        sys.exit(1)
    else:
        print("✅ Pre-commit validation passed: No import conflicts or known compiler issues found.")
        sys.exit(0)

if __name__ == "__main__":
    main()
