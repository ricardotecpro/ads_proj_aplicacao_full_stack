import glob
import re
import os

def process_slides():
    # Attempt to find the slides source directory
    base_dir = "docs/slides"
    src_dir = os.path.join(base_dir, "src")
    
    if not os.path.exists(src_dir):
        # Fallback to base slides dir if src doesn't exist
        src_dir = base_dir
        
    files = glob.glob(os.path.join(src_dir, "*.md"))
    
    print(f"Found {len(files)} slide files in {src_dir}")

    for file_path in files:
        if "index.md" in file_path: continue
        
        with open(file_path, 'r', encoding='utf-8') as f:
            content = f.read()
        
        # 1. First, convert any existing broken .fragment syntax
        content = content.replace('{ .fragment }', '<!-- .element: class="fragment" -->')
        
        lines = content.splitlines()
        new_lines = []
        in_code_block = False
        
        for line in lines:
            stripped = line.strip()
            
            # Toggle code block status
            if stripped.startswith('```'):
                in_code_block = not in_code_block
                new_lines.append(line)
                continue
            
            # If in code block, do nothing
            if in_code_block:
                new_lines.append(line)
                continue
            
            # Check for list items
            # Matches: "- Item", "* Item", "1. Item"
            is_list_item = (stripped.startswith('- ') or stripped.startswith('* ') or re.match(r'^\d+\. ', stripped))
            is_horizontal_rule = stripped == '---' or stripped == '***'
            has_fragment = 'class="fragment"' in line
            
            if is_list_item and not is_horizontal_rule and not has_fragment:
                # Add fragment element syntax
                new_lines.append(line.rstrip() + ' <!-- .element: class="fragment" -->')
            else:
                new_lines.append(line)
                
        with open(file_path, 'w', encoding='utf-8') as f:
            f.write('\n'.join(new_lines) + '\n')
        print(f"Processed {os.path.basename(file_path)}")

if __name__ == "__main__":
    process_slides()
