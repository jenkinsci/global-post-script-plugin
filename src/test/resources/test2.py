str = 'dropdeploy to: '
if 'dropdeploy_targets' in locals():
    str += dropdeploy_targets
if 'manager' in locals():
    str += ", " + manager.getCause()
print str